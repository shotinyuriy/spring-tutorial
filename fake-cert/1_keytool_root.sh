# On Windows run on Git Bash
ls -l
echo "===== ====="
source ./0_var_passwd.sh
source ./0_var_files.sh
if [[ "$(pwd)" =~ ^.*/fake-cert$ ]]; then
  echo "" > ${TRUST_STORE}.log
  # Root CA
  keytool -genkeypair -keyalg RSA -keysize 2048 -alias root-ca \
  -storetype PKCS12 -keystore ${TRUST_STORE} -storepass ${TRUST_STORE_PASS} \
  -ext bc=ca:true \
  -ext ku:c=dig,keyEncipherment \
  -dname "CN=mock.root.ca,OU=Mock Root CA,O=Mock Company,L=Mock City,S=Mock State,C=Mock Country" \
  -validity 365;
  # Root CA
  keytool -list -alias root-ca -v -keystore ${TRUST_STORE} -storepass ${TRUST_STORE_PASS} >> ${TRUST_STORE}.log

  # Intermediate CA
  keytool -genkeypair -storetype PKCS12 -keyalg RSA -keysize 2048 -alias intermediate-ca \
  -keystore ${TRUST_STORE} -storepass ${TRUST_STORE_PASS} \
  -ext bc=ca:true \
  -dname "CN=mock.intermediate.ca,OU=Mock Intermediate CA,O=Mock Company,L=Mock City,S=Mock State,C=Mock Country" \
  -validity 365;
  # Intermediate CA
  keytool -list -alias intermediate-ca -v -keystore ${TRUST_STORE} -storepass ${TRUST_STORE_PASS} >> ${TRUST_STORE}.log
  # Intermediate CA
  keytool -certreq -alias intermediate-ca -file intermediate_ca.csr \
  -keystore ${TRUST_STORE} -storepass ${TRUST_STORE_PASS};
  # Intermediate CA
  keytool -gencert \
  -infile intermediate_ca.csr -outfile intermediate_ca_cert.pem \
  -alias root-ca \
  -ext BC=0 \
  -rfc \
  -keystore ${TRUST_STORE} -storepass ${TRUST_STORE_PASS};
  # Intermediate CA
  keytool -exportcert -rfc -file root_ca_cert.pem \
  -alias root-ca \
  -keystore ${TRUST_STORE} -storepass ${TRUST_STORE_PASS};
  # Intermediate CA
  cat root_ca_cert.pem intermediate_ca_cert.pem > intermediate_ca_chain_cert.pem
  # Intermediate CA
  keytool -importcert -file intermediate_ca_chain_cert.pem -alias intermediate-ca \
  -keystore ${TRUST_STORE} -storepass ${TRUST_STORE_PASS} -noprompt;
  # Intermediate CA
  keytool -list -alias intermediate-ca -v -keystore ${TRUST_STORE} -storepass ${TRUST_STORE_PASS} >> ${TRUST_STORE}.log
else
  echo 'run this script from the "*/**/fake-cert" directory'
  exit 1
fi