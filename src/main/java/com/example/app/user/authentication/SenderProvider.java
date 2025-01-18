package com.example.app.user.authentication;

import com.example.app.user.api.UserProfile;
import io.fluxcapacitor.common.MessageType;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.common.HasMessage;
import io.fluxcapacitor.javaclient.common.serialization.DeserializingMessage;
import io.fluxcapacitor.javaclient.tracking.handling.authentication.AbstractUserProvider;
import io.fluxcapacitor.javaclient.tracking.handling.authentication.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SenderProvider extends AbstractUserProvider {

    public SenderProvider() {
        super(Sender.class);
    }

    @Override
    public User fromMessage(HasMessage message) {
        try {
            if (message instanceof DeserializingMessage dm && dm.getMessageType() == MessageType.WEBREQUEST) {
                return AuthenticationUtils.getSender(dm);
            }
            return super.fromMessage(message);
        } catch (Throwable e) {
            log.error("Failed to get sender", e);
            throw e;
        }
    }

    @Override
    public User getUserById(Object userId) {
        UserProfile userProfile = FluxCapacitor.loadAggregate(userId, UserProfile.class).get();
        return userProfile == null ? null : Sender.builder().userId(userProfile.getUserId())
                .userRole(userProfile.getUserRole()).operator(userProfile.getOperator()).build();
    }

    @Override
    public User getSystemUser() {
        return Sender.system;
    }
}
