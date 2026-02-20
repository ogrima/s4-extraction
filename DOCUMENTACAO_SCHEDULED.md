# Documentação do Processo Agendado (Scheduled Process)

Este documento descreve o funcionamento dos processos agendados no sistema `s4-extraction`, detalhando como os dados são extraídos da API remota, processados e sincronizados com o Elasticsearch.

O sistema possui dois fluxos principais de agendamento:
1. **Extração de Eventos (ScheduledExtraction)**: Busca dados de logs de acesso da API remota e salva no banco de dados local.
2. **Sincronização com ELK (ElkSync)**: Pega os dados salvos localmente e os envia para o Elasticsearch.

---

## 1. Extração de Eventos (`ScheduledExtraction`)

O processo de extração é responsável por manter o banco de dados local atualizado com os últimos movimentos detectados nos dispositivos (spots).

### Fluxo de Funcionamento:
1. **Identificação do Spot**: Atualmente, o processo está configurado para processar uma lista de IDs de spots (ex: `spotId = 1`).
2. **Verificação de Estado (`CtlExtraction`)**:
   - O sistema consulta a tabela `ctl_extraction` para verificar qual foi a última posição (`last_position`) extraída para aquele spot.
3. **Busca de Dados**:
   - **Primeira Carga**: Se não houver registro para o spot, o sistema chama `loadAllEvents` (limite de 1000 registros).
   - **Cargas Incrementais**: Se já existir um registro, o sistema utiliza o `last_position` como `offset` para buscar apenas os eventos novos através de `loadEventsSince`.
4. **Processamento e Filtro**:
   - Para cada log recebido (`AccessLogs`), o sistema verifica se o `userId` (ID do hardware da Tag) existe na base local de Tags.
   - Se a Tag for reconhecida, um registro é criado na tabela `tracking`.
5. **Atualização de Controle**:
   - Após o processamento bem-sucedido, o `last_position` na tabela `ctl_extraction` é atualizado com o maior ID extraído, garantindo que a próxima execução comece de onde esta parou.

### Configuração:
- **Frequência**: Atualmente comentada no código para execução manual ou via cron, mas planejada para rodar a cada 30 minutos (nos minutos 0 e 30).
- **Service**: `EventLoaderService` realiza as chamadas REST e a lógica de persistência.

---

## 2. Sincronização com Elasticsearch (`ElkSync`)

Este processo garante que os dados de rastreamento (`tracking`) estejam disponíveis no Kibana para visualização e análise.

### Fluxo de Funcionamento:
1. **Busca de Dados Locais**:
   - O processo busca na tabela `tracking` todos os registros que possuem `external_id` maior ou igual a um valor configurado (`elk.sync.from-external-id`).
2. **Enriquecimento de Dados**:
   - Para cada registro de `tracking`, o sistema busca informações adicionais:
     - **Tag**: Nome, Label, Hardware ID, Status.
     - **Spot**: Nome, Label, Hardware ID, Status, ID do Espaço, Criticidade.
3. **Formatação JSON**:
   - Constrói um objeto JSON plano contendo todas as informações do rastreamento e do hardware relacionado.
   - Adiciona campos de timestamp formatados para compatibilidade com o ELK (`@timestamp`).
4. **Envio via REST**:
   - Realiza um POST para o endpoint do Elasticsearch (`/_doc`).
5. **Log de Operação**:
   - Registra no log do sistema a quantidade de registros enviados com sucesso e se houve falhas.

### Configuração:
- **Frequência**: Executa a cada 1 minuto (`cron = "0 0/1 * * * *"`).
- **Destino**: URL e Índice configurados via `application.properties` (`elk.sync.es-base-url` e `elk.sync.es-index`).

---

## Resumo das Entidades Envolvidas

- **AccessLogs**: Representação do dado bruto vindo da API externa.
- **CtlExtraction**: Tabela de controle que armazena o cursor (offset) da última extração por spot.
- **Tracking**: Dados de movimentação processados e salvos localmente.
- **Tag / Spot**: Entidades de infraestrutura que fornecem contexto aos eventos de tracking.
