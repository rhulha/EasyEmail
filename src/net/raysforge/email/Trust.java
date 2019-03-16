package net.raysforge.email;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.TrustManagerFactory;

import com.sun.mail.util.MailSSLSocketFactory;

public class Trust {

	public static MailSSLSocketFactory getTrustedCertificateMailSSLSocketFactory(String certFile, Properties props) {
		try (FileInputStream fis = new FileInputStream(certFile)) {
			X509Certificate ca = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new BufferedInputStream(fis));

			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(null, null);
			ks.setCertificateEntry(Integer.toString(1), ca);

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);

			MailSSLSocketFactory mailSSLSocketFactory = new MailSSLSocketFactory();
			mailSSLSocketFactory.setTrustManagers(tmf.getTrustManagers());
			return mailSSLSocketFactory;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
