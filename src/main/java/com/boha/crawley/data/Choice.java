package com.boha.crawley.data;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Choice {

@SerializedName("index")
@Expose
private Integer index;
@SerializedName("message")
@Expose
private Message message;
@SerializedName("finish_reason")
@Expose
private String finishReason;

public Integer getIndex() {
return index;
}

public void setIndex(Integer index) {
this.index = index;
}

public Message getMessage() {
return message;
}

public void setMessage(Message message) {
this.message = message;
}

public String getFinishReason() {
return finishReason;
}

public void setFinishReason(String finishReason) {
this.finishReason = finishReason;
}

}