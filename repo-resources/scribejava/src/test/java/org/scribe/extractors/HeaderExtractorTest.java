package org.scribe.extractors;

import static org.junit.Assert.*;

import org.junit.*;
import org.scribe.exceptions.*;
import org.scribe.model.*;
import org.scribe.test.helpers.*;

public class HeaderExtractorTest
{

  private HeaderExtractorImpl extractor;
  private OAuthRequest request;

  @Before
  public void setup()
  {
    request = ObjectMother.createSampleOAuthRequest();
    extractor = new HeaderExtractorImpl();
  }

  @Test
  public void shouldExtractStandardHeader()
  {
    /*String expected = "OAuth oauth_callback=\"http%3A%2F%2Fexample%2Fcallback\", " + "oauth_signature=\"OAuth-Signature\", "
        + "oauth_consumer_key=\"AS%23%24%5E%2A%40%26\", " + "oauth_timestamp=\"123456\"";
    String header = extractor.extract(request);
    assertEquals(expected, header);*/
    String start = "OAuth ";
    String expected1 = "oauth_callback=\"http%3A%2F%2Fexample%2Fcallback\"";
    String expected2 = "oauth_signature=\"OAuth-Signature\"";
    String expected3 = "oauth_consumer_key=\"AS%23%24%5E%2A%40%26\"";
    String expected4 = "oauth_timestamp=\"123456\"";
    String header = extractor.extract(request);
    assertTrue(header.startsWith(start));
    assertTrue(header.contains(expected1));
    assertTrue(header.contains(expected2));
    assertTrue(header.contains(expected3));
    assertTrue(header.contains(expected4));
    assertEquals(4, header.split(",").length);

  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldExceptionIfRequestIsNull()
  {
    OAuthRequest nullRequest = null;
    extractor.extract(nullRequest);
  }

  @Test(expected = OAuthParametersMissingException.class)
  public void shouldExceptionIfRequestHasNoOAuthParams()
  {
    OAuthRequest emptyRequest = new OAuthRequest(Verb.GET, "http://example.com");
    extractor.extract(emptyRequest);
  }
}
