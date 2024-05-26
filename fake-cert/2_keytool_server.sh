# On Windows run on Git Bash
source ./0_var_passwd.sh
source ./0_var_files.sh
if [[ "$(pwd)" =~ ^.*/fake-cert$ ]]; then
  # Log
  echo "" > ${server_key_store_file}.log

  # Server Certificate: key pair
  keytool -genkeypair -alias server \
  -storetype PKCS12 \
  -keyalg RSA -keysize 2048 \
  -keystore ${server_key_store_file} -storepass ${KEY_STORE_PASS} \
  -dname "CN=mock.cotc.rest,OU=REST Mock,O=Mock Company,L=Mock City,S=Mock State,C=Mock Country" \
  -ext eku=sa \
  -ext ku:c=dig,keyEncipherment \
  -ext san=dns:localhost,ip:127.0.0.1,dns:${HOSTNAME} \
  -validity 365;

  # Server Certificate: signing request
  keytool -certreq -alias server -file ${server_csr_file} \
  -keystore ${server_key_store_file} -storepass ${KEY_STORE_PASS};

  # Server Certificate: Making intermediate-ca generate and signing the server certificate
  keytool -gencert \
  -infile ${server_csr_file} -outfile ${server_cert_file} \
  -alias intermediate-ca \
  -ext ku:c=dig,keyEncipherment \
  -rfc \
  -keystore ${trust_store_file} -storepass ${TRUST_STORE_PASS};

  # Server Certificate: Chain
  cat ${server_cert_file} ${root_ca_cert_file}> ${server_cert_chain_file};

  # Server Certificate : Updating server public key
  keytool -importcert -file ${server_cert_chain_file} -alias server \
  -keystore ${server_key_store_file} -storepass ${KEY_STORE_PASS} -noprompt;

  # Server Certificate: Checking server keychain
  keytool -list -v -alias server \
  -keystore ${server_key_store_file} -storepass ${KEY_STORE_PASS} >> ${server_key_store_file}.log;

  # Server Certificate: Exporting private key
  openssl pkcs12 -in ${server_key_store_file} -passin "env:KEY_STORE_PASS" -nocerts -nodes -out ${server_key_file}
else
  echo 'run this script from the "*/**/fake-cert" directory'
  exit 1
fi