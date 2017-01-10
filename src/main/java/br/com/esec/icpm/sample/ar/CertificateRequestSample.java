package br.com.esec.icpm.sample.ar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.ws.Service;

import br.com.esec.icpm.mcc.ws.AsymmetricCipherType;
import br.com.esec.icpm.mcc.ws.CSRReqType;
import br.com.esec.icpm.mcc.ws.CSRReqTypeV2;
import br.com.esec.icpm.mcc.ws.CSRRespType;
import br.com.esec.icpm.mcc.ws.CSRStatusReqType;
import br.com.esec.icpm.mcc.ws.CSRStatusRespType;
import br.com.esec.icpm.mcc.ws.CSRStatusRespTypeV2;
import br.com.esec.icpm.mcc.ws.FindUserReqType;
import br.com.esec.icpm.mcc.ws.FindUserRespType;
import br.com.esec.icpm.mcc.ws.RegistryAuthorityPortType;
import br.com.esec.icpm.mcc.ws.RegistryAuthorityPortTypeV2;
import br.com.esec.icpm.server.factory.Status;
import br.com.esec.icpm.server.ws.ICPMException;
import br.com.esec.icpm.server.ws.MobileUserType;

public class CertificateRequestSample {

	public static void main(String[] args) throws InterruptedException,
			IOException {
		if (args.length < 3) {
			System.out
					.println("CertificateRequestSample need this params: [uniqueIdentifier] [keyType] [acId] [timeout]");
			System.exit(1);
		}

		// Set the arguments
		String uniqueIdentifier = args[0];
		AsymmetricCipherType keyType = AsymmetricCipherType.valueOf(args[1]);
		long acId = Long.parseLong(args[2]);
		int timeout = Integer.parseInt(args[3]);
		// Get the registryAuthorityPort
		String endpointAddr = "http://localhost:8280/mcc/serviceRa.wsdl";

		System.out.println("Connecting to Registry Authority Service... " + endpointAddr);
		System.out.println("\n\n");
		URL url = new URL(endpointAddr);
		Service registryAuthorityService = Service.create(url,
				RegistryAuthorityPortTypeV2.QNAME);
		RegistryAuthorityPortTypeV2 registryAuthorityPortV2 = registryAuthorityService
				.getPort(RegistryAuthorityPortTypeV2.class);

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
			findUserResp = registryAuthorityPortV2.findUser(findUserReq);
			// Shows the client data
			System.out.println("Client's name: "
					+ findUserResp.getUserInfo().getName() + "\n\n");

			// Start the key generation
			CSRReqTypeV2 csrReq = new CSRReqTypeV2();
			CSRRespType csrResp = null;

			// Set the CSR data
			csrReq.setMobileUser(mobileUser);
			csrReq.setKeyType(keyType);
			csrReq.setTimeOut(timeout);
			if(acId!=0){
				csrReq.setCaId(acId);
			}
			// Request the key geeration
			System.out.println("Requesting CSR generation...");
			// Notify the mobile user of the request
			csrResp = registryAuthorityPortV2.csrRequest(csrReq);
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
			CSRStatusRespTypeV2 csrStatusResp = null;
			csrStatusReq.setCertificateRequestId(certificateRequestId);

			// Check the request of the CSR on the server
			// Stop when the transaction is completed
			System.out.println("Veryfing status request...");
			do {
				csrStatusResp = registryAuthorityPortV2
						.csrStatusQuery(csrStatusReq);
				System.out.println("Waiting response");
				System.out.println("Mobile Status is: "+csrStatusResp.getStatus().getMobileStatus());
				// Stop for 10 seconds
				Thread.sleep(10000);
			} while (csrStatusResp.getStatus().getStatusCode() == Status.TRANSACTION_IN_PROGRESS.getCode());

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
