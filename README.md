# Mod - Training Mod (Forge 1.8.9)

Mod server-side para treino de mira/PvP com um BOT IA humanoide.

## Requisitos do ambiente

- Minecraft Forge: `1.8.9-11.15.1.2318`
- Java para build do MDK antigo: **JDK 8**

## Comandos

- `/trainingmod spawn <hard|easy|normal|extreme>`
- `/trainingmod knockback <default|hacking|long-normal>`

## Comportamentos

- Bot renderizado com modelo de player.
- Skin do bot usa a skin do jogador que spawnou.
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
- Alguns ataques podem falhar para deixar mais humanizado.

## 

