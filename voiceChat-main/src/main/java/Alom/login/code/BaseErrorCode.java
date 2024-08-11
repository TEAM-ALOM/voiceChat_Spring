package Alom.login.code;

import Alom.login.dto.ErrorReasonDto.ErrorReasonDto;

public interface BaseErrorCode {
    public ErrorReasonDto getReason();
    public ErrorReasonDto getReasonHttpStatus();
}
