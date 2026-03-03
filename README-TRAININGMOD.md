# Training Mod (Forge 1.8.9)

Projeto client-side para treino de mira/PvP com NPC humanoide.

## Requisitos do ambiente

- Minecraft Forge: `1.8.9-11.15.1.2318`
- Java para build do MDK antigo: **JDK 8**

## Comandos

- `/trainingmod spawn <hard|easy|normal|extreme>`
- `/trainingmod knockback <default|hacking|long-normal>`

## Comportamento implementado

- NPC nao-zombie, renderizado com modelo de player.
- Skin do NPC usa o perfil (skin) do jogador que spawnou.
- NPC persegue e ataca o jogador mais proximo.
- Dificuldade altera alcance de hit do NPC:
  - `easy`: 2.6
  - `normal`: 3.0
  - `hard`: 3.35
  - `extreme`: 3.7
- Knockback recebido pelo NPC:
  - `default`: padrao
  - `hacking`: quase sem knockback
  - `long-normal`: knockback normal um pouco mais longo
- Ataques podem falhar ocasionalmente para ficar menos mecanico.

## Estrutura de codigo

- `br.mod.trainingmod.TrainingMod` - entrada do mod.
- `br.mod.trainingmod.proxy` - registro client-side.
- `br.mod.trainingmod.command` - comando `/trainingmod`.
- `br.mod.trainingmod.entity` - entidade do NPC.
- `br.mod.trainingmod.entity.ai` - IA de combate.
- `br.mod.trainingmod.session` - estado da sessao de treino.
- `br.mod.trainingmod.client.render` - render humanoide + skin.

