import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

/**
 * This class provide various static methods that relax X509 certificate and
 * hostname verification while using the SSL over the HTTP protocol.
 *
 * @author    Francis Labrie
 * @author    fridtjof updated to use javax.net.ssl.* instead of internal sun APIs
 */
public final class SSLUtilities {

    /**
     * Hostname verifier for the Sun's deprecated API.
     *
     * @deprecated see {@link #_hostnameVerifier}.
     */
    private static javax.net.ssl.HostnameVerifier __hostnameVerifier;
    /**
     * Thrust managers for the Sun's deprecated API.
     *
     * @deprecated see {@link #_trustManagers}.
     */
    private static javax.net.ssl.TrustManager[] __trustManagers;
    /**
     * Hostname verifier.
     */
    private static HostnameVerifier _hostnameVerifier;
    /**
     * Thrust managers.
     */
    private static TrustManager[] _trustManagers;


    /**
     * Set the default Hostname Verifier to an instance of a fake class that
     * trust all hostnames. This method uses the old deprecated API from the
     * com.sun.ssl package.
     *
     * @deprecated see {@link #_trustAllHostnames()}.
     */
    private static void __trustAllHostnames() {
        // Create a trust manager that does not validate certificate chains
        if(__hostnameVerifier == null) {
            __hostnameVerifier = new _FakeHostnameVerifier();
        } // if
        // Install the all-trusting host name verifier
        javax.net.ssl.HttpsURLConnection.
                setDefaultHostnameVerifier(__hostnameVerifier);
    } // __trustAllHttpsCertificates

    /**
     * Set the default X509 Trust Manager to an instance of a fake class that
     * trust all certificates, even the self-signed ones. This method uses the
     * old deprecated API from the com.sun.ssl package.
     *
     * @deprecated see {@link #_trustAllHttpsCertificates()}.
     */
    private static void __trustAllHttpsCertificates() {
        javax.net.ssl.SSLContext context;

        // Create a trust manager that does not validate certificate chains
        if(__trustManagers == null) {
            __trustManagers = new javax.net.ssl.TrustManager[]
                    {new _FakeX509TrustManager()};
        } // if
        // Install the all-trusting trust manager
        try {
            context = javax.net.ssl.SSLContext.getInstance("SSL");
            context.init(null, __trustManagers, new SecureRandom());
        } catch(GeneralSecurityException gse) {
            throw new IllegalStateException(gse.getMessage());
        } // catch
        javax.net.ssl.HttpsURLConnection.
                setDefaultSSLSocketFactory(context.getSocketFactory());
    } // __trustAllHttpsCertificates

    /**
     * Return true if the protocol handler property java.
     * protocol.handler.pkgs is set to the Sun's com.sun.net.ssl.
     * internal.www.protocol deprecated one, false
     * otherwise.
     *
     * @return                true if the protocol handler
     * property is set to the Sun's deprecated one, false
     * otherwise.
     */
    private static boolean isDeprecatedSSLProtocol() {
        return("com.sun.net.ssl.internal.www.protocol".equals(System.
                getProperty("java.protocol.handler.pkgs")));
    } // isDeprecatedSSLProtocol

    /**
     * Set the default Hostname Verifier to an instance of a fake class that
     * trust all hostnames.
     */
    private static void _trustAllHostnames() {
        // Create a trust manager that does not validate certificate chains
        if(_hostnameVerifier == null) {
            _hostnameVerifier = new FakeHostnameVerifier();
        } // if
        // Install the all-trusting host name verifier:
        HttpsURLConnection.setDefaultHostnameVerifier(_hostnameVerifier);
    } // _trustAllHttpsCertificates

    /**
     * Set the default X509 Trust Manager to an instance of a fake class that
     * trust all certificates, even the self-signed ones.
     */
    private static void _trustAllHttpsCertificates() {
        SSLContext context;

        // Create a trust manager that does not validate certificate chains
        if(_trustManagers == null) {
            _trustManagers = new TrustManager[] {new FakeX509TrustManager()};
        } // if
        // Install the all-trusting trust manager:
        try {
            context = SSLContext.getInstance("SSL");
            context.init(null, _trustManagers, new SecureRandom());
        } catch(GeneralSecurityException gse) {
            throw new IllegalStateException(gse.getMessage());
        } // catch
        HttpsURLConnection.setDefaultSSLSocketFactory(context.
                getSocketFactory());
    } // _trustAllHttpsCertificates

    /**
     * Set the default Hostname Verifier to an instance of a fake class that
     * trust all hostnames.
     */
    public static void trustAllHostnames() {
        // Is the deprecated protocol setted?
        if(isDeprecatedSSLProtocol()) {
            __trustAllHostnames();
        } else {
            _trustAllHostnames();
        } // else
    } // trustAllHostnames

    /**
     * Set the default X509 Trust Manager to an instance of a fake class that
     * trust all certificates, even the self-signed ones.
     */
    public static void trustAllHttpsCertificates() {
        // Is the deprecated protocol setted?
        if(isDeprecatedSSLProtocol()) {
            __trustAllHttpsCertificates();
        } else {
            _trustAllHttpsCertificates();
        } // else
    } // trustAllHttpsCertificates

    /**
     * This class implements a fake hostname verificator, trusting any host
     * name. This class uses the old deprecated API from the com.sun.
     * ssl package.
     *
     * @author    Francis Labrie
     *
     * @deprecated see {@link SSLUtilities.FakeHostnameVerifier}.
     */
    public static class _FakeHostnameVerifier
            implements javax.net.ssl.HostnameVerifier {

        /**
         * Always return true, indicating that the host name is an
         * acceptable match with the server's authentication scheme.
         *
         * @param hostname        the host name.
         * @param session         the SSL session used on the connection to
         * host.
         * @return                the true boolean value
         * indicating the host name is trusted.
         */
        public boolean verify(String hostname, SSLSession session) {
            return(true);
        } // verify
    } // _FakeHostnameVerifier


    /**
     * This class allow any X509 certificates to be used to authenticate the
     * remote side of a secure socket, including self-signed certificates. This
     * class uses the old deprecated API from the com.sun.ssl
     * package.
     *
     * @author    Francis Labrie
     *
     * @deprecated see {@link SSLUtilities.FakeX509TrustManager}.
     */
    public static class _FakeX509TrustManager
            implements javax.net.ssl.X509TrustManager {

        /**
         * Empty array of certificate authority certificates.
         */
        private static final X509Certificate[] _AcceptedIssuers =
                new X509Certificate[] {};

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        /**
         * Return an empty array of certificate authority certificates which
         * are trusted for authenticating peers.
         *
         * @return                a empty array of issuer certificates.
         */
        public X509Certificate[] getAcceptedIssuers() {
            return(_AcceptedIssuers);
        } // getAcceptedIssuers
    } // _FakeX509TrustManager


    /**
     * This class implements a fake hostname verificator, trusting any host
     * name.
     *
     * @author    Francis Labrie
     */
    public static class FakeHostnameVerifier implements HostnameVerifier {

        /**
         * Always return true, indicating that the host name is
         * an acceptable match with the server's authentication scheme.
         *
         * @param hostname        the host name.
         * @param session         the SSL session used on the connection to
         * host.
         * @return                the true boolean value
         * indicating the host name is trusted.
         */
        public boolean verify(String hostname,
                              javax.net.ssl.SSLSession session) {
            return(true);
        } // verify
    } // FakeHostnameVerifier


    /**
     * This class allow any X509 certificates to be used to authenticate the
     * remote side of a secure socket, including self-signed certificates.
     *
     * @author    Francis Labrie
     */
    public static class FakeX509TrustManager implements X509TrustManager {

        /**
         * Empty array of certificate authority certificates.
         */
        private static final X509Certificate[] _AcceptedIssuers =
                new X509Certificate[] {};


        /**
         * Always trust for client SSL chain peer certificate
         * chain with any authType authentication types.
         *
         * @param chain           the peer certificate chain.
         * @param authType        the authentication type based on the client
         * certificate.
         */
        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) {
        } // checkClientTrusted

        /**
         * Always trust for server SSL chain peer certificate
         * chain with any authType exchange algorithm types.
         *
         * @param chain           the peer certificate chain.
         * @param authType        the key exchange algorithm used.
         */
        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) {
        } // checkServerTrusted

        /**
         * Return an empty array of certificate authority certificates which
         * are trusted for authenticating peers.
         *
         * @return                a empty array of issuer certificates.
         */
        public X509Certificate[] getAcceptedIssuers() {
            return(_AcceptedIssuers);
        } // getAcceptedIssuers
    } // FakeX509TrustManager
} // SSLUtilities