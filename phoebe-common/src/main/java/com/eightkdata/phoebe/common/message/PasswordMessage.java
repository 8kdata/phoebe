package com.eightkdata.phoebe.common.message;

import com.eightkdata.phoebe.common.Encoders;
import com.eightkdata.phoebe.common.Message;
import com.eightkdata.phoebe.common.FeBeMessageType;
import com.google.common.base.MoreObjects;

import java.nio.charset.Charset;

public class PasswordMessage implements Message {

    private final Boolean encrypted;

    private final String password;

    public PasswordMessage(String password) {
        this(password, null);
    }

    public PasswordMessage(String password, Boolean encrypted) {
        this.encrypted = encrypted;
        this.password = password;
    }

    public Boolean isEncrypted() {
        return encrypted;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public FeBeMessageType getType() {
        return FeBeMessageType.PasswordMessage;
    }

    @Override
    public int computePayloadLength(Charset encoding) {
        return Encoders.stringLength(password, encoding);
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(getType().name());
        helper.add("password", encrypted != null && encrypted ? password : "********");
        return helper.toString();
    }

}
