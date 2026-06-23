# Exercício 12 - Manipulação básica de threads


## Objetivo

Com base no exemplo `SimpleThreads`, foi adicionada uma nova thread chamada `CpuIntensiveThread`. Ela executa uma tarefa intensiva em CPU: procura números primos a partir de um valor grande.

A thread principal controla um tempo limite de execução. Quando o tempo é excedido, ela chama `interrupt()` nas threads que ainda estão em execução. A tarefa de CPU verifica periodicamente se foi interrompida usando `Thread.currentThread().isInterrupted()` e encerra de forma segura.

## Estrutura

```text
exercicio12-simplethreads/
├── README.md
├── Makefile
├── src/
│   └── SimpleThreads.java
└── .vscode/
    ├── launch.json
    └── tasks.json
```

## Como compilar e executar pelo terminal

```bash
javac -d out src/SimpleThreads.java
java -cp out SimpleThreads 5
```

O argumento `5` significa que o programa deve esperar no máximo 5 segundos antes de solicitar a interrupção das threads que ainda estiverem executando.

## Como executar usando Makefile

```bash
make run
```

ou escolhendo o tempo limite:

```bash
make run ARGS=5
```

## Como limpar os arquivos compilados

```bash
make clean
```
