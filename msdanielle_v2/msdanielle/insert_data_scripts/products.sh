echo 'Inserindo produto 1'
curl -X GET 'http://localhost:9092/add/produto01/produto01/100/149.99'
echo ''

echo 'Inserindo produto 2'
curl -X GET 'http://localhost:9092/add/produto02/produto02/75/139.99'
echo ''

echo 'Inserindo produto 3'
curl -X GET 'http://localhost:9092/add/produto03/produto03/50/249.99'
echo ''

echo 'Lista de produtos'
curl -X GET 'http://localhost:9092/getProducts'
echo ''