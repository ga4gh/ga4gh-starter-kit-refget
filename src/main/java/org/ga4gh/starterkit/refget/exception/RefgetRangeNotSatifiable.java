package org.ga4gh.starterkit.refget.exception;

import org.ga4gh.starterkit.common.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
public class RefgetRangeNotSatifiable extends CustomException {
    private static final long serialVersionUID = 1L;

    public RefgetRangeNotSatifiable() {
        super();
    }

    public RefgetRangeNotSatifiable(String message) {
        super(message);
    }

    public RefgetRangeNotSatifiable(Throwable cause) {
        super(cause);
    }

}
