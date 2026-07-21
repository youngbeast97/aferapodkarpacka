package model.session;

import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    public SessionResponse toResponse(Session session) {

        if (session == null) {
            return null;
        }

       SessionResponse response = new SessionResponse();

        response.setId(session.getId());
        response.setClientId(session.getClient().getId());
        response.setWorkerId(session.getWorker().getId());
        response.setRoomId(session.getRoom().getId());

        response.setStartTime(session.getStartTime());
        response.setEndTime(session.getEndTime());
        response.setDurationMinutes(session.getDurationMinutes());
        response.setTotalCost(session.getTotalCost());
        response.setInfected(session.isInfected());

        return response;
    }
}