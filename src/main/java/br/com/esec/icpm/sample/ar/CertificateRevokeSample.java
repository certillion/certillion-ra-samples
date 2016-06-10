package br.com.esec.icpm.sample.ar;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import br.com.esec.icpm.mcc.ws.CertificateRevokedType;
import br.com.esec.icpm.mcc.ws.RegistryAuthorityPortType;
import br.com.esec.icpm.server.ws.ICPMException;
import br.com.esec.icpm.server.ws.StatusRespType;

public class CertificateRevokeSample {

	public static void main(String[] args) throws MalformedURLException {
		if (args.length < 1) {
			System.out
					.println("CertificateRevokeSample need this params: [serial] [issuerDN]");
			System.exit(1);
		}

		// Set the arguments
		String serial = args[0];
		String issuerDN = args[1];

		// Get the registryAuthorityPort
		String endpointAddr = "http://localhost:8280/mcc/serviceRa.wsdl";

		System.out.println("Connecting to Registry Authority Service... " + endpointAddr);
		System.out.println("\n\n");

		URL url = new URL(endpointAddr);
		QName qName = new QName("http://esec.com.br/mcc/ra",
				"RegistryAuthorityService");
		Service registryAuthorityService = Service.create(url, qName);
		RegistryAuthorityPortType registryAuthorityPort = registryAuthorityService
				.getPort(RegistryAuthorityPortType.class);

		// Inform to MCS that the certificate was revoked
		CertificateRevokedType certificateRevoked = new CertificateRevokedType();
		StatusRespType status = null;
		// Set data of the revoked certificate
		certificateRevoked.setIssuerDN(issuerDN);
		certificateRevoked.setSerial(serial);

		try {
			System.out.println("Sending notification...");
			// Get status of the transaction
			status = registryAuthorityPort
					.certificateRevoked(certificateRevoked);

			System.out.println("Status " + status.getStatus().getStatusCode()
					+ " " + status.getStatus().getStatusDetail());

		} catch (ICPMException e) {
			System.err.println("Error " + e.getFaultInfo().getStatusCode()
					+ " " + e.getFaultInfo().getStatusDetail());
		}

	}

}
