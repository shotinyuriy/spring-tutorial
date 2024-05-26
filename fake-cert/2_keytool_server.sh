# On Windows run on Git Bash
source ./0_var_passwd.sh
source ./0_var_files.sh
if [[ "$(pwd)" =~ ^.*/fake-cert$ ]]; then
  # Server Certificate
  keytool -genkeypair -alias server \
  -storetype PKCS12 \
  -keyalg RSA -keysize 2048 \
  -keystore ${KEY_STORE} -storepass ${KEY_STORE_PASS} \
  -dname "CN=mock.cotc.rest,OU=REST Mock,O=Mock Company,L=Mock City,S=Mock State,C=Mock Country" \
  -ext eku=sa \
  -ext ku:c=dig,keyEncipherment \
  -ext san=dns:localhost,ip:127.0.0.1,dns:${HOSTNAME} \
  -validity 365;
  # Server Certificate: signing request
  keytool -certreq -alias server -file server.csr \
  -keystore ${KEY_STORE} -storepass ${KEY_STORE_PASS};
  # Server Certificate: Making intermediate-ca generate and signing the server certificate
  keytool -gencert \
  -infile server.csr -outfile server_cert.pem \
  -alias intermediate-ca \
  -ext ku:c=dig,keyEncipherment \
  -rfc \
  -keystore ${TRUST_STORE} -storepass ${TRUST_STORE_PASS};
  # Server Certificate: Chain
  cat server_cert.pem root_ca_cert.pem > server_cert_chain.pem;

  # Server Certificate : Updating server public key
  keytool -importcert -file server_cert_chain.pem -alias server \
  -keystore ${KEY_STORE} -storepass ${KEY_STORE_PASS} -noprompt;
  # Server Certificate: Checking server keychain
  keytool -list -v -alias server \
  -keystore ${KEY_STORE} -storepass ${KEY_STORE_PASS};
else
  echo 'run this script from the "*/**/fake-cert" directory'
  exit 1
fi