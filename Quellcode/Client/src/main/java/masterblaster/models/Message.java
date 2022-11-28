package masterblaster.models;

import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;

public class Message {
    private Integer messageId;
    private Integer id;
    private String text;

    public Message(Integer messageId, Integer id, String text){
        this.messageId = messageId;
        this.id = id;
        this.text = text;
    }

    public Message(JsonObject json) {
        if (json.has("messageId"))
            this.setMessageId(json.get("messageId").getAsInt());
        if (json.has("id"))
            this.setId(json.get("id").getAsInt());
        if (json.has("text"))
            this.setText(json.get("text").getAsString());
    }

    public JSONObject toJSONObject(){
        JSONObject json = new JSONObject();

        if (this.getMessageId() != null)
            json.put("messageId", this.getMessageId());
        if (this.getId() != null)
            json.put("id", this.getId());
        if (this.text != null)
            json.put("text", this.getText());
        return json;
    }



    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
