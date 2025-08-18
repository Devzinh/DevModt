# DevModt

Um sistema profissional de gerenciamento de MOTD dinâmico para servidores Minecraft.

## Descrição

O DevModt é um plugin para Minecraft que oferece um sistema avançado de gerenciamento de MOTD (Message of the Day) dinâmico. O plugin permite que administradores de servidor configurem e personalizem mensagens dinâmicas que são exibidas aos jogadores, proporcionando uma experiência mais envolvente e informativa.

## Funcionalidades

- ✨ **MOTD Dinâmico**: Sistema avançado de mensagens personalizáveis
- 🎯 **Gerenciamento de Eventos**: Configuração de MOTDs específicos para eventos
- 📊 **Sistema de Métricas**: Coleta e análise de estatísticas de uso
- 🔄 **Atualizações Automáticas**: Verificação e notificação de novas versões
- 📝 **Sistema de Placeholders**: Suporte a variáveis dinâmicas nas mensagens
- 📈 **Histórico**: Rastreamento de mudanças e histórico de MOTDs
- 🎮 **Preview**: Visualização prévia das configurações antes da aplicação
- 🔧 **API Completa**: Interface de programação para desenvolvedores
- 💾 **Banco de Dados**: Armazenamento persistente com SQLite

## Tecnologias Utilizadas

- **Linguagem**: Java 21
- **Plataforma**: Spigot/Bukkit API 1.21
- **Build System**: Gradle com Groovy DSL
- **Banco de Dados**: SQLite (via sqlite-jdbc 3.44.1.0)
- **Ferramentas de Desenvolvimento**: 
  - Plugin run-paper para testes
  - Sistema de métricas integrado
  - Verificador de atualizações

## Como Instalar e Rodar

### Pré-requisitos

- Java 21 ou superior
- Servidor Minecraft 1.21 com Spigot/Paper
- Gradle (incluído via wrapper)

### Instalação

1. **Clone o repositório:**
   ```bash
   git clone <url-do-repositorio>
   cd DevModt
   ```

2. **Compile o plugin:**
   ```bash
   # Linux/Mac
   ./gradlew build
   
   # Windows
   gradlew.bat build
   ```

3. **Localize o arquivo JAR:**
   O plugin compilado estará em `build/libs/DevModt-1.0.jar`

4. **Instale no servidor:**
   - Copie o arquivo JAR para a pasta `plugins/` do seu servidor
   - Reinicie o servidor

### Desenvolvimento

Para testar durante o desenvolvimento:

```bash
# Executar servidor de desenvolvimento
./gradlew runServer

# Limpar build anterior
./gradlew clean
```

## Uso

### Comandos Principais

O plugin oferece o comando principal `/devmotd` com os seguintes subcomandos:

```
/devmotd reload          - Recarrega as configurações
/devmotd setevent        - Define MOTD para eventos específicos
/devmotd clearevent      - Remove configurações de eventos
/devmotd preview         - Visualiza preview das configurações
/devmotd stats           - Exibe estatísticas de uso
/devmotd metrics         - Mostra métricas detalhadas
/devmotd history         - Visualiza histórico de mudanças
/devmotd placeholder     - Gerencia placeholders personalizados
/devmotd test            - Testa configurações
/devmotd update          - Verifica atualizações
/devmotd info            - Informações do plugin
```

**Aliases disponíveis:** `/dmotd`, `/dynamicmotd`

### Permissões

- `devmotd.admin` - Acesso completo aos comandos (padrão: OP)
- `devmotd.api` - Uso da API do plugin (padrão: false)
- `devmotd.stats` - Visualização de estatísticas (padrão: OP)
- `devmotd.update` - Recebimento de notificações de atualização (padrão: OP)

### Exemplo de Uso

```bash
# Configurar um evento especial
/devmotd setevent natal "🎄 Feliz Natal! Servidor em festa! 🎁"

# Visualizar preview
/devmotd preview

# Verificar estatísticas
/devmotd stats

# Recarregar configurações
/devmotd reload
```

## Estrutura de Pastas

```
DevModt/
├── src/main/java/me/devplugins/devModt/
│   ├── DevModt.java              # Classe principal do plugin
│   ├── api/                      # API pública do plugin
│   │   ├── DevModtAPI.java       # Interface principal da API
│   │   └── events/               # Eventos customizados
│   ├── commands/                 # Sistema de comandos
│   │   └── CommandHandler.java   # Processador de comandos
│   ├── listeners/                # Event listeners
│   ├── managers/                 # Gerenciadores do sistema
│   │   ├── ConfigManager.java    # Gerenciamento de configurações
│   │   ├── DatabaseManager.java  # Gerenciamento do banco de dados
│   │   ├── EventManager.java     # Gerenciamento de eventos
│   │   ├── MOTDManager.java      # Gerenciamento do MOTD
│   │   ├── MetricsManager.java   # Sistema de métricas
│   │   └── PlaceholderManager.java # Gerenciamento de placeholders
│   └── utils/                    # Utilitários
│       ├── Scheduler.java        # Agendador de tarefas
│       └── UpdateChecker.java    # Verificador de atualizações
├── src/main/resources/
│   └── plugin.yml                # Configurações do plugin
├── build.gradle                  # Configurações de build
└── README.md                     # Este arquivo
```

## Contribuição

Contribuições são bem-vindas! Para contribuir:

1. **Fork** o projeto
2. Crie uma **branch** para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. **Commit** suas mudanças (`git commit -am 'Adiciona nova funcionalidade'`)
4. **Push** para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um **Pull Request**

### Reportando Bugs

- Use as **Issues** do GitHub para reportar bugs
- Inclua informações detalhadas sobre o problema
- Forneça logs relevantes quando possível
- Especifique a versão do plugin e do servidor

### Sugerindo Funcionalidades

- Abra uma **Issue** com a tag "enhancement"
- Descreva detalhadamente a funcionalidade proposta
- Explique como ela beneficiaria os usuários

## Licença

Este projeto está licenciado sob a [Licença MIT](LICENSE) - veja o arquivo LICENSE para detalhes.

## Contato

- **Desenvolvedor**: DevPlugins
- **Discord**: [Servidor da Comunidade](https://discord.gg/A4F9jtGhFU)
- **Versão**: 1.0
- **Minecraft**: 1.21

---

**DevModt** - Transformando a experiência de MOTD em servidores Minecraft! 🚀