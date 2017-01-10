package br.com.esec.icpm.sample.ar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.xml.ws.Service;

import br.com.esec.icpm.mcc.ws.CertificateIssuedType;
import br.com.esec.icpm.mcc.ws.RegistryAuthorityPortType;
import br.com.esec.icpm.mcc.ws.RegistryAuthorityPortTypeV2;
import br.com.esec.icpm.server.ws.ICPMException;
import br.com.esec.icpm.server.ws.StatusRespType;

public class CertificateIssuedSample {

	public static void main(String[] args) throws MalformedURLException,
			FileNotFoundException, CertificateException {
		if (args.length < 2) {
			System.out
					.println("CertificateIssuedSample need this params: [certificateRequestId] [certificatePath]");
			System.exit(1);
		}

		// Set the arguments
		Long certificateRequestId = new Long(args[0]);
		String certificate = args[1];

		// Get the generated certificate
		InputStream x509 = new FileInputStream(certificate);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate) cf.generateCertificate(x509);

		// Get the registryAuthorityPort
		String endpointAddr = "http://localhost:8280/mcc/serviceRa.wsdl";

		System.out.println("Connecting to Registry Authority Service... " + endpointAddr);
		System.out.println("\n\n");
		URL url = new URL(endpointAddr);
		Service registryAuthorityService = Service.create(url,
				RegistryAuthorityPortTypeV2.QNAME);
		RegistryAuthorityPortTypeV2 registryAuthorityPort = registryAuthorityService
				.getPort(RegistryAuthorityPortTypeV2.class);

		// Send the issued certificate to MCC
		CertificateIssuedType certificateIssued = new CertificateIssuedType();
		certificateIssued.setCertificate(cert.getEncoded());
		certificateIssued.setCertificateRequestId(certificateRequestId);

		try {
			System.out.println("Sending generated certificate to MCC...");
			System.out.println("\n\n");

			// Get status of the transaction
			StatusRespType statusResp = registryAuthorityPort
					.certificateIssued(certificateIssued);

			System.out.println("Status "
					+ statusResp.getStatus().getStatusCode() + " "
					+ statusResp.getStatus().getStatusDetail());

		} catch (ICPMException e) {
			System.err.println("Error " + e.getFaultInfo().getStatusCode()
					+ " " + e.getFaultInfo().getStatusDetail());
		}

	}

}
