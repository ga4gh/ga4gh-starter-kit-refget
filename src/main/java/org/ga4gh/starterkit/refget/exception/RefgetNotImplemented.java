package org.ga4gh.starterkit.refget.exception;

import org.ga4gh.starterkit.common.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED)
public class RefgetNotImplemented extends CustomException {
    private static final long serialVersionUID = 1L;

    public RefgetNotImplemented() {
        super();
    }

    public RefgetNotImplemented(String message) {
        super(message);
    }

    public RefgetNotImplemented(Throwable cause) {
        super(cause);
    }

}
