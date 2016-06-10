package br.com.esec.icpm.sample.ar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.ws.Service;

import br.com.esec.icpm.mcc.ws.AsymmetricCipherType;
import br.com.esec.icpm.mcc.ws.CSRReqType;
import br.com.esec.icpm.mcc.ws.CSRRespType;
import br.com.esec.icpm.mcc.ws.CSRStatusReqType;
import br.com.esec.icpm.mcc.ws.CSRStatusRespType;
import br.com.esec.icpm.mcc.ws.FindUserReqType;
import br.com.esec.icpm.mcc.ws.FindUserRespType;
import br.com.esec.icpm.mcc.ws.RegistryAuthorityPortType;
import br.com.esec.icpm.server.factory.Status;
import br.com.esec.icpm.server.ws.ICPMException;
import br.com.esec.icpm.server.ws.MobileUserType;

public class CertificateRequestSample {

	public static void main(String[] args) throws InterruptedException,
			IOException {
		if (args.length < 2) {
			System.out
					.println("CertificateRequestSample need this params: [uniqueIdentifier] [keyType] [acId]");
			System.exit(1);
		}

		// Set the arguments
		String uniqueIdentifier = args[0];
		AsymmetricCipherType keyType = AsymmetricCipherType.valueOf(args[1]);
		long acId = Long.parseLong(args[2]);

		// Get the registryAuthorityPort
		String endpointAddr = "http://localhost:8280/mcc/serviceRa.wsdl";

		System.out.println("Connecting to Registry Authority Service... " + endpointAddr);
		System.out.println("\n\n");
		URL url = new URL(endpointAddr);
		Service registryAuthorityService = Service.create(url,
				RegistryAuthorityPortType.QNAME);
		RegistryAuthorityPortType registryAuthorityPort = registryAuthorityService
				.getPort(RegistryAuthorityPortType.class);

		try {
			// Request to MCS the client data
			FindUserReqType findUserReq = new FindUserReqType();
			// Set the client's phone number
			MobileUserType mobileUser = new MobileUserType();
			mobileUser.setUniqueIdentifier(uniqueIdentifier);
			findUserReq.setMobileUser(mobileUser);
			FindUserRespType findUserResp = null;

			// Get the client data
			System.out.println("Requesting the client data...");
			findUserResp = registryAuthorityPort.findUser(findUserReq);
			// Shows the client data
			System.out.println("Client's name: "
					+ findUserResp.getUserInfo().getName() + "\n\n");

			// Start the key generation
			CSRReqType csrReq = new CSRReqType();
			CSRRespType csrResp = null;

			// Set the CSR data
			csrReq.setMobileUser(mobileUser);
			csrReq.setKeyType(keyType);
			
			if(acId!=0){
				csrReq.setCaId(acId);
			}
			// Request the key geeration
			System.out.println("Requesting CSR generation...");
			// Notify the mobile user of the request
			csrResp = registryAuthorityPort.csrRequest(csrReq);
			// Set the certificateRequestId
			Long certificateRequestId = csrResp.getCertificateRequestId();
			// Code for the user to perform the generation of the CSR
			System.out.println("certificateRequestId = " + certificateRequestId);
			System.out.println("PIN = " + csrResp.getPin());
			System.out.println("\n\n");
			System.out
					.println("Insert PIN on the mobile and press Enter to continue...");
			System.in.read();

			// Start the request verification
			CSRStatusReqType csrStatusReq = new CSRStatusReqType();
			CSRStatusRespType csrStatusResp = null;
			csrStatusReq.setCertificateRequestId(certificateRequestId);

			// Check the request of the CSR on the server
			// Stop when the transaction is completed
			System.out.println("Veryfing status request...");
			do {
				csrStatusResp = registryAuthorityPort
						.csrStatusQuery(csrStatusReq);
				System.out.println("Waiting response");
				// Stop for 10 seconds
				Thread.sleep(10000);
			} while (csrStatusResp.getStatus().equals(
					Status.TRANSACTION_IN_PROGRESS));

			System.out.println("\n\n");

			FileOutputStream out = new FileOutputStream("csr-" + certificateRequestId + ".csr");
			out.write(csrStatusResp.getCsr());
			out.close();
			
			System.out.println("CSR generated successfully!");
		} catch (ICPMException e) {
			System.err.println("Error " + e.getFaultInfo().getStatusCode()
					+ " " + e.getFaultInfo().getStatusDetail());
		}
		
		

	}

}
