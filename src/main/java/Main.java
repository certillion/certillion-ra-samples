import java.util.Arrays;

import br.com.esec.icpm.sample.ar.CertificateIssuedSample;
import br.com.esec.icpm.sample.ar.CertificateRequestSample;
import br.com.esec.icpm.sample.ar.CertificateRevokeSample;

public class Main {

	private static final String COMMAND_REQUEST = "request";
	private static final String COMMAND_ISSUED = "issued";
	private static final String COMMAND_REVOKE = "revoke";

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("Need this params: [command] [options...]");
			System.out.println("\tcommand: [" + COMMAND_REQUEST + ", " + COMMAND_ISSUED + ", " + COMMAND_REVOKE + "] ");
			System.exit(1);
		}

		String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
		if (COMMAND_REQUEST.equals(args[0])) {
			CertificateRequestSample.main(newArgs);
		} else if (COMMAND_ISSUED.equals(args[0])) {
			CertificateIssuedSample.main(newArgs);
		} else if (COMMAND_REVOKE.equals(args[0])) {
			CertificateRevokeSample.main(newArgs);
		}else{
			System.out.println("Need this params: [command] [options...]");
		}
	}

}
