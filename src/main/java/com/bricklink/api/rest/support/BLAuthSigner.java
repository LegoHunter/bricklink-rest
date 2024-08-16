package com.bricklink.api.rest.support;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// @See https://medium.com/@prasad.veluru/guide-to-authenticate-requests-using-oauth-1-0-231e4894311f
public class BLAuthSigner {
	private static final String	CHARSET			= "UTF-8";
	private static final String	HMAC_SHA1		= "HmacSHA1";
	private static final String	EMPTY_STRING	= "";
	private static final String	CARRIAGE_RETURN	= "\r\n";

	private String				signMethod		= "HMAC-SHA1";
	private String				version			= "1.0";
	private String				consumerKey;
	private String				consumerSecret;
	private String				tokenValue;
	private String				tokenSecret;

	private String				url;
	private String				verb;

	private Map<String, String>	oauthParameters;
	private Map<String, String>	queryParameters;

	private Timer				timer;

	public BLAuthSigner(String consumerKey, String consumerSecret) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.oauthParameters = new HashMap<>();
		this.queryParameters = new HashMap<>();
		this.timer = new Timer();
	}

	public void setToken( String tokenValue, String tokenSecret ) {
		this.tokenValue = tokenValue;
		this.tokenSecret = tokenSecret;
	}
	
	public void setURL( String url ) {
		this.url = url;
	}
	
	public void setVerb( String verb ) {
		this.verb = verb;
	}

	public void addParameter( String key, String value ) {
		queryParameters.put( key, value );
	}

	public Map<String, String> getFinalOAuthParams( ) throws Exception {
		String signature = computeSignature();
		
		Map<String, String> params = new HashMap<>();
		params.putAll( oauthParameters );
		params.put( OAuthConstants.SIGNATURE, signature);

		return params;
	}

	public String computeSignature( ) throws Exception {
		addOAuthParameter( OAuthConstants.VERSION, version );
		addOAuthParameter( OAuthConstants.TIMESTAMP, getTimestampInSeconds() );
		addOAuthParameter( OAuthConstants.NONCE, getNonce() );
		addOAuthParameter( OAuthConstants.TOKEN, tokenValue );
		addOAuthParameter( OAuthConstants.CONSUMER_KEY, consumerKey );
		addOAuthParameter( OAuthConstants.SIGN_METHOD, signMethod );

		String baseString = getBaseString();
		String keyString = OAuthEncoder.encode( consumerSecret ) + '&' + OAuthEncoder.encode( tokenSecret );
		String signature = doSign( baseString, keyString );

		return URLEncoder.encode(signature, "UTF-8");
	}

	private void addOAuthParameter( String key, String value ) {
		oauthParameters.put( key, value );
	}

	private String getTimestampInSeconds( ) {
		Long ts = timer.getMilis();
		return String.valueOf( TimeUnit.MILLISECONDS.toSeconds( ts ) );
	}

	private String getNonce( ) {
		Long ts = timer.getMilis();
		return String.valueOf( ts + Math.abs( timer.getRandomInteger() ) );
	}


	private String getBaseString( ) {
		String params = Stream.of(oauthParameters, queryParameters)
				.map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue, (k1, k2) -> k1))
				.entrySet()
				.stream()
				.map(e -> OAuthEncoder.encode( e.getKey() ).concat( "=" ).concat( e.getValue() ))
				.sorted()
				.collect(Collectors.joining("&"));

		String formUrlEncodedParams = OAuthEncoder.encode(params);
		String sanitizedURL = OAuthEncoder.encode( url.replaceAll( "\\?.*", "" ).replace( "\\:\\d{4}", "" ) );

		return  "%s&%s&%s".formatted(verb, sanitizedURL, formUrlEncodedParams);
	}

	private String doSign( String toSign, String keyString ) throws Exception {
		SecretKeySpec key = new SecretKeySpec( (keyString).getBytes( CHARSET ), HMAC_SHA1 );
		Mac mac = Mac.getInstance( HMAC_SHA1 );
		mac.init( key );
		byte[] bytes = mac.doFinal( toSign.getBytes( CHARSET ) );
		return bytesToBase64String( bytes ).replace( CARRIAGE_RETURN, EMPTY_STRING );
	}

	private String bytesToBase64String( byte[] bytes ) throws Exception {
		return new String( Base64.encodeBase64( bytes ), "UTF-8" );
	}

	static class Timer {
		private final Random	rand	= new Random();

		Long getMilis( ) {
			return System.currentTimeMillis();
		}

		Integer getRandomInteger( ) {
			return rand.nextInt();
		}
	}

	static class OAuthEncoder {
		private static final Map<String, String>	ENCODING_RULES;

		static {
			Map<String, String> rules = new HashMap<String, String>();
			rules.put( "*", "%2A" );
			rules.put( "+", "%20" );
			rules.put( "%7E", "~" );
			ENCODING_RULES = Collections.unmodifiableMap( rules );
		}

		public static String encode( String plain ) {
			String encoded = null;
			try {
				encoded = URLEncoder.encode( plain, CHARSET );
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}

			for( Map.Entry<String, String> rule : ENCODING_RULES.entrySet() ) {
				encoded = encoded.replaceAll( Pattern.quote( rule.getKey() ), rule.getValue() );
			}
			return encoded;
		}
	}
}
