if [[ ! -f "./.secret" ]]; then
echo "export TRUST_STORE_PASS='$(date +%S_%m_%d_%M)'" >> ./.secret
echo "export KEY_STORE_PASS='$(date +%S_%m_%d_%M)'" >> ./.secret
echo "generate new .secret; please set up values in that file"
exit 1
fi
source ./.secret