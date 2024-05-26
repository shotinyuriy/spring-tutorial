# On Windows run on Git Bash
ls -l
echo "===== ====="
source ./0_var_passwd.sh
source ./0_var_files.sh
if [[ "$(pwd)" =~ ^.*/fake-cert$ ]]; then
  # Log
  echo "" > ${trust_store_file}.log
  # Root CA : key pair
  keytool -genkeypair -keyalg RSA -keysize 2048 -alias root-ca \
  -storetype PKCS12 -keystore ${trust_store_file} -storepass ${TRUST_STORE_PASS} \
  -ext bc=ca:true \
  -ext ku:c=dig,keyEncipherment \
  -dname "CN=mock.root.ca,OU=Mock Root CA,O=Mock Company,L=Mock City,S=Mock State,C=Mock Country" \
  -validity 365;

  # Root CA
  keytool -list -alias root-ca -v -keystore ${trust_store_file} -storepass ${TRUST_STORE_PASS} >> ${trust_store_file}.log

  # Intermediate CA : key pair
  keytool -genkeypair -storetype PKCS12 -keyalg RSA -keysize 2048 -alias intermediate-ca \
  -keystore ${trust_store_file} -storepass ${TRUST_STORE_PASS} \
  -ext bc=ca:true \
  -dname "CN=mock.intermediate.ca,OU=Mock Intermediate CA,O=Mock Company,L=Mock City,S=Mock State,C=Mock Country" \
  -validity 365;

  # Intermediate CA
  keytool -list -alias intermediate-ca -v -keystore ${trust_store_file} -storepass ${TRUST_STORE_PASS} >> ${trust_store_file}.log

  # Intermediate CA : request
  keytool -certreq -alias intermediate-ca -file ${intermediate_ca_csr_file} \
  -keystore ${trust_store_file} -storepass ${TRUST_STORE_PASS};

  # Intermediate CA : generate
  keytool -gencert \
  -infile ${intermediate_ca_csr_file} -outfile ${intermediate_ca_cert_file} \
  -alias root-ca \
  -ext BC=0 \
  -rfc \
  -keystore ${trust_store_file} -storepass ${TRUST_STORE_PASS};

  # Intermediate CA : export
  keytool -exportcert -rfc -file ${root_ca_cert_file} \
  -alias root-ca \
  -keystore ${trust_store_file} -storepass ${TRUST_STORE_PASS};

  # Intermediate CA : cert chain
  cat ${root_ca_cert_file} ${intermediate_ca_cert_file} > ${intermediate_ca_chain_cert_file}

  # Intermediate CA : import
  keytool -importcert -file ${intermediate_ca_chain_cert_file} -alias intermediate-ca \
  -keystore ${trust_store_file} -storepass ${TRUST_STORE_PASS} -noprompt;

  # Intermediate CA : list
  keytool -list -alias intermediate-ca -v -keystore ${trust_store_file} -storepass ${TRUST_STORE_PASS} >> ${trust_store_file}.log
else
  echo 'run this script from the "*/**/fake-cert" directory'
  exit 1
fi