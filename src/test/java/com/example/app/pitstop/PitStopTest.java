package com.example.app.pitstop;

import com.example.app.pitstop.api.IncidentId;
import com.example.app.pitstop.api.command.AcceptOffer;
import com.example.app.pitstop.api.command.CloseIncident;
import com.example.app.pitstop.api.command.OfferAssistance;
import com.example.app.pitstop.api.command.ReportIncident;
import com.example.app.pitstop.api.query.GetIncidents;
import com.example.app.user.api.UserId;
import com.example.app.user.authentication.AuthenticationUtils;
import io.fluxcapacitor.javaclient.test.TestFixture;
import io.fluxcapacitor.javaclient.web.HttpRequestMethod;
import io.fluxcapacitor.javaclient.web.WebRequest;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import io.fluxcapacitor.javaclient.tracking.handling.authentication.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.app.pitstop.IncidentLifecycleHandler.INCIDENT_DEADLINE;

class PitStopTest {

    final TestFixture testFixture = TestFixture.create(PitStopApi.class, IncidentLifecycleHandler.class);

    @Nested
    class IncidentTests {

        @BeforeEach
        void setUp() {
            testFixture.givenCommands("/refdata/register-operators.json");
        }

        @Test
        void reportIncident() {
            testFixture.whenCommand("/pitstop/report-incident.json")
                    .expectEvents("/pitstop/report-incident.json");
        }

        @Test
        void reportIncidentTwiceNotAllowed() {
            testFixture
                    .givenCommands("/pitstop/report-incident.json")
                    .whenCommand("/pitstop/report-incident.json")
                    .expectExceptionalResult(IllegalCommandException.class);
        }

        @Test
        void reportIncidentWithoutUserNotAllowed() {
            testFixture.whenCommandByUser(null, "/pitstop/report-incident.json")
                    .expectExceptionalResult(UnauthorizedException.class);
        }

        @Test
        void reportViaApi() {
            testFixture.whenPost("/api/incidents", "/pitstop/incident-details.json")
                    .expectEvents(ReportIncident.class).expectResult(IncidentId.class);
        }

        @Nested
        class LifecycleTests {
            @BeforeEach
            void setUp() {
                testFixture
                        .givenCommands("/user/create-user.json")
                        .givenCommandsByUser("user", "/pitstop/report-incident.json");
            }

            @Test
            void closeAutomaticallyOnDeadline() {
                testFixture.whenTimeElapses(INCIDENT_DEADLINE)
                        .expectEvents(CloseIncident.class);
            }

            @Test
            void dontCloseIncidentTwice() {
                testFixture
                        .givenCommands("/pitstop/close-incident.json")
                        .whenTimeElapses(INCIDENT_DEADLINE)
                        .expectNoCommands();
            }
        }

        @Nested
        class SearchTests {
            @BeforeEach
            void setUp() {
                testFixture
                        .givenCommands("/user/create-user.json",
                                       "/user/create-other-user.json")
                        .givenCommandsByUser("user", "/pitstop/report-incident.json");
            }

            @Test
            void getIncidents() {
                testFixture.whenQuery(new GetIncidents())
                        .expectResult(r -> r.size() == 1
                                           && Objects.equals(r.getFirst().getIncidentId(), new IncidentId("case123")));
            }

            @Test
            void getIncidents_authorized() {
                testFixture.whenQueryByUser("user", new GetIncidents())
                        .expectResult(r -> r.size() == 1
                                           && Objects.equals(r.getFirst().getIncidentId(), new IncidentId("case123")));
            }

            @Test
            void getIncidents_unauthorized() {
                testFixture.whenQueryByUser("other", new GetIncidents()).expectResult(List::isEmpty);
            }

            @Test
            void getIncidents_operator() {
                testFixture
                        .givenCommandsByUser("other", "/refdata/register-operator-aaa-with-other-owner.json")
                        .whenQueryByUser("other", new GetIncidents()).expectResult(r -> r.size() == 1);
            }

            @Test
            void getViaApi() {
                testFixture.whenGet("/api/incidents").<List<?>>expectResult(r -> r.size() == 1);
            }
        }

        @Nested
        class AssistanceOfferTests {

            @BeforeEach
            void setUp() {
                testFixture
                        .givenCommands("/user/create-user.json")
                        .givenCommandsByUser("user", "/pitstop/report-incident.json");
            }

            @Test
            void offerAssistance() {
                testFixture.whenCommand("/pitstop/offer-assistance-allstate.json")
                        .expectEvents("/pitstop/offer-assistance-allstate.json")
                        .andThen()
                        .whenCommand("/pitstop/offer-assistance-aaa.json")
                        .expectEvents("/pitstop/offer-assistance-aaa.json");
            }

            @Test
            void offerAssistanceTwiceFails() {
                testFixture.givenCommands("/pitstop/offer-assistance-allstate.json")
                        .whenCommand("/pitstop/offer-assistance-allstate.json")
                        .expectExceptionalResult(IllegalCommandException.class);
            }

            @Test
            void offerAssistanceUnauthorized() {
                testFixture
                        .whenCommandByUser("user", "/pitstop/offer-assistance-allstate.json")
                        .expectExceptionalResult(IllegalCommandException.class);
            }

            @Test
            void offerAssistanceAuthorized() {
                testFixture.givenCommands("/refdata/register-operator-with-owner.json")
                        .whenCommandByUser("user", "/pitstop/offer-assistance-allstate.json")
                        .expectEvents("/pitstop/offer-assistance-allstate.json");
            }

            @Test
            void acceptOffer() {
                testFixture.givenCommands("/pitstop/offer-assistance-allstate.json",
                                          "/pitstop/offer-assistance-aaa.json")
                        .whenCommand("/pitstop/accept-offer-aaa.json")
                        .expectEvents("/pitstop/accept-offer-aaa.json");
            }

            @Test
            void acceptMultipleOffersFails() {
                testFixture.givenCommands("/pitstop/offer-assistance-allstate.json",
                                          "/pitstop/offer-assistance-aaa.json", "/pitstop/accept-offer-aaa.json")
                        .whenCommand("/pitstop/accept-offer-allstate.json")
                        .expectExceptionalResult(IllegalCommandException.class);
            }

            @Test
            void offerViaApi() {
                testFixture.whenPost("/api/incidents/%s/offers".formatted("case123"),
                                     "/pitstop/offer-details.json")
                        .expectEvents(OfferAssistance.class);
            }

            @Test
            void acceptViaApi() {
                testFixture.givenCommands("/pitstop/offer-assistance-allstate.json")
                        .whenPost("/api/incidents/%s/offers/%s/accept".formatted("case123", "offer123-allstate"),
                                  Map.of()).expectEvents(AcceptOffer.class);
            }


            @Nested
            class AcceptedOfferTests {
                @BeforeEach
                void setUp() {
                    testFixture.givenCommands("/user/create-other-user.json",
                                              "/refdata/register-operator-aaa-with-other-owner.json",
                                              "/pitstop/offer-assistance-allstate.json",
                                              "/pitstop/offer-assistance-aaa.json",
                                              "/pitstop/accept-offer-aaa.json");
                }

                @Test
                void closeOffer_reporter() {
                    testFixture.whenCommandByUser("user", "/pitstop/close-incident.json")
                            .expectEvents("/pitstop/close-incident.json");
                }

                @Test
                void closeOffer_operator() {
                    testFixture.whenCommandByUser("other", "/pitstop/close-incident.json")
                            .expectEvents("/pitstop/close-incident.json");
                }

                @Test
                void closeViaApi_reporter() {
                    testFixture
                            .withHeader("Authorization", createAuthorizationHeader("user"))
                            .whenPost("/api/incidents/%s/close".formatted("case123"),
                                         Map.of()).expectEvents(CloseIncident.class);
                }
            }
        }
    }

    String createAuthorizationHeader(String user) {
        return testFixture.getFluxCapacitor().apply(
                fc -> AuthenticationUtils.createAuthorizationHeader(new UserId("user")));
    }

    @Test
    void testCorsPreflight() {
        testFixture.whenWebRequest(WebRequest.builder().method(HttpRequestMethod.OPTIONS)
                                           .url("/api/user").build())
                .expectSuccessfulResult();
    }
}