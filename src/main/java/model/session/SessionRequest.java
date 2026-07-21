package model.session;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionRequest {

    @NotNull
    private Long clientId;

    @NotNull
    private Long workerId;

    @NotNull
    private Long roomId;
}