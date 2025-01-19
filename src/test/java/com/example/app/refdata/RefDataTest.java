package com.example.app.refdata;

import com.example.app.refdata.api.OperatorId;
import com.example.app.user.api.command.AuthorizeForOperator;
import io.fluxcapacitor.javaclient.test.TestFixture;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.example.app.user.authentication.AuthenticationUtils.createAuthorizationHeader;

public class RefDataTest {

    final TestFixture testFixture = TestFixture.create(RefDataApi.class);

    @Test
    void registerOperators() {
        testFixture.whenCommand("/refdata/register-operators.json")
                .expectEvents("/refdata/register-operator-allstate.json");
    }

    @Test
    void registerOperatorWithOwner() {
        testFixture.givenCommands("/user/create-user.json")
                .whenCommand("/refdata/register-operator-with-owner.json")
                .expectEvents("/refdata/register-operator-with-owner.json", AuthorizeForOperator.class);
    }

    @Test
    void registerOperatorWithOwner_nonExistentOwner() {
        testFixture
                .whenCommand("/refdata/register-operator-with-owner.json")
                .expectNoEvents().expectExceptionalResult(IllegalCommandException.class);
    }

    @Nested
    class ApiTests {
        @Test
        void registerOperator() {
            testFixture.givenCommands("/user/create-user.json")
                    .withHeader("Authorization", createAuthorizationHeader("user"))
                    .whenPost("/api/operator", "/refdata/operator-details.json")
                    .expectResult(OperatorId.class);
        }
    }

}
