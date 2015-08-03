package com.eightkdata.phoebe.common.message;

import com.eightkdata.phoebe.common.Encoders;
import com.eightkdata.phoebe.common.FeBeMessage;
import com.eightkdata.phoebe.common.FeBeMessageType;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

public class ErrorResponse implements FeBeMessage {

    // todo: the field codes should probably use an enum

    private final Map<Byte, String> fields;

    public ErrorResponse(Map<Byte, String> fields) {
        this.fields = Collections.unmodifiableMap(fields);
    }

    public String getSeverity() {
        return fields.get((byte) 'S');
    }

    public String getCode() {
        return fields.get((byte) 'C');
    }

    public String getMessage() {
        return fields.get((byte) 'M');
    }

    public String getDetail() {
        return fields.get((byte) 'D');
    }

    @Override
    public FeBeMessageType getType() {
        return FeBeMessageType.ErrorResponse;
    }

    @Override
    public int computePayloadLength(Charset encoding) {
        int length = 0;
        for (Map.Entry<Byte, String> field : fields.entrySet()) {
            length += 1 + Encoders.stringLength(field.getValue(), encoding);
        }
        length += 1; // zero byte terminator
        return length;
    }

    @Override
    public String toString() {
        return "ErrorResponse(" + getSeverity() + "," + getCode() + "," + getMessage() + ")";
    }

}
