package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * RFC 9110 "problem details" body returned with HTTP 400, e.g.
 * {"title": "One or more validation errors occurred.", "status": 400,
 *  "errors": {"Iso2": ["Iso2 must be 3 characters long."]}, "traceId": "00-..."}.
 * Errors not tied to one parameter are listed under the empty key "".
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationErrorResponse {

    private String type;
    private String title;
    private int status;
    private Map<String, List<String>> errors;
    private String traceId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, List<String>> errors) {
        this.errors = errors;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
