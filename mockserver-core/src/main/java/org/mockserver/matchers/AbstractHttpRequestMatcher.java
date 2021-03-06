package org.mockserver.matchers;

import org.mockserver.logging.MockServerLogger;
import org.mockserver.mock.Expectation;
import org.mockserver.model.RequestDefinition;

import java.util.Objects;

import static org.mockserver.character.Character.NEW_LINE;

public abstract class AbstractHttpRequestMatcher extends NotMatcher<RequestDefinition> implements HttpRequestMatcher {

    protected static final String REQUEST_DID_NOT_MATCH = "request:{}didn't match request matcher";
    protected static final String EXPECTATION_DID_NOT_MATCH = "request:{}didn't match expectation";
    protected static final String BECAUSE = ":{}because:{}";
    protected static final String EXPECTATION_DID_NOT_MATCH_WITHOUT_BECAUSE = "request:{}didn't match expectation:{}";
    protected static final String WITHOUT_BECAUSE = ":{}";
    protected static final String REQUEST_DID_MATCH = "request:{}matched request:{}";
    protected static final String EXPECTATION_DID_MATCH = "request:{}matched expectation:{}";
    protected static final String DID_NOT_MATCH = " didn't match";
    protected static final String MATCHED = " matched";
    protected static final String COLON_NEW_LINE = ": " + NEW_LINE;
    protected static final String COLON_NEW_LINES = ": " + NEW_LINE + NEW_LINE;

    protected final MockServerLogger mockServerLogger;
    private int hashCode;
    private boolean isBlank = false;
    private boolean responseInProgress = false;
    protected boolean controlPlaneMatcher;
    protected Expectation expectation;
    protected String didNotMatchRequestBecause = REQUEST_DID_NOT_MATCH + BECAUSE;
    protected String didNotMatchExpectationBecause = EXPECTATION_DID_NOT_MATCH + BECAUSE;
    protected String didNotMatchExpectationWithoutBecause = EXPECTATION_DID_NOT_MATCH_WITHOUT_BECAUSE + WITHOUT_BECAUSE;

    protected AbstractHttpRequestMatcher(MockServerLogger mockServerLogger) {
        this.mockServerLogger = mockServerLogger;
    }

    @Override
    public boolean update(Expectation expectation) {
        if (this.expectation != null && this.expectation.equals(expectation)) {
            return false;
        } else {
            this.controlPlaneMatcher = false;
            this.expectation = expectation;
            this.hashCode = 0;
            this.isBlank = expectation.getHttpRequest() == null;
            return apply(expectation.getHttpRequest());
        }
    }


    @Override
    public boolean update(RequestDefinition requestDefinition) {
        this.controlPlaneMatcher = true;
        this.expectation = null;
        this.hashCode = 0;
        this.isBlank = requestDefinition == null;
        return apply(requestDefinition);
    }

    public void setControlPlaneMatcher(boolean controlPlaneMatcher) {
        this.controlPlaneMatcher = controlPlaneMatcher;
    }

    public void setDescription(String description) {
        didNotMatchRequestBecause = REQUEST_DID_NOT_MATCH + " " + description.trim() + BECAUSE;
        didNotMatchExpectationBecause = EXPECTATION_DID_NOT_MATCH + " " + description.trim() + BECAUSE;
        didNotMatchExpectationWithoutBecause = EXPECTATION_DID_NOT_MATCH_WITHOUT_BECAUSE + " " + description.trim() + WITHOUT_BECAUSE;
    }

    abstract boolean apply(RequestDefinition requestDefinition);

    @Override
    public boolean matches(RequestDefinition requestDefinition) {
        return matches(null, requestDefinition);
    }

    @Override
    public abstract boolean matches(MatchDifference matchDifference, RequestDefinition requestDefinition);

    @Override
    public Expectation getExpectation() {
        return expectation;
    }

    public boolean isResponseInProgress() {
        return responseInProgress;
    }

    public HttpRequestMatcher setResponseInProgress(boolean responseInProgress) {
        this.responseInProgress = responseInProgress;
        return this;
    }

    public boolean isBlank() {
        return isBlank;
    }

    @Override
    public boolean isActive() {
        return expectation == null || expectation.isActive();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (hashCode() != o.hashCode()) {
            return false;
        }
        HttpRequestPropertiesMatcher that = (HttpRequestPropertiesMatcher) o;
        return Objects.equals(expectation, that.expectation);
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = Objects.hash(expectation);
        }
        return hashCode;
    }
}
