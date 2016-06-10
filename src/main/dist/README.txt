Conteúdo:

	/src

		Diretório contendo todo o código usado nos exemplos mencionados nos manuais.

	certillion-ra-samples.jar

		Artefato executável com o seguintes comandos:

			\> java -jar certillion-ra-samples.jar request [phoneNumber] [keyType] [acName]
				phoneNumber: Número de telefone registrado no sistema
				keyType: RSA2048 ou ECC256

			\> java -jar certillion-ra-samples.jar issued [certificateRequestId] [certificatePath]
				certificateRequestId: Identificador da requisição gerada acima
				certificatePath: Caminho do certificado expedido a partir do CSR gerado na requisição acima

			\> java -jar certillion-ra-samples.jar revoke [serial] [issuerDN]
				serial: serial do certificado a ser revogado
				issuerDN: 'Domain Name' do expedidor
