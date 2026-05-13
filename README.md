AGGIORNAMENTO MAGGIO 2026

Input + finestra

Classe Window che crea lo JFrame con dimensioni fisse (1000×563).

Classe KeyInput che intercetta W/A/S/D e imposta handler.setUp/Down/Left/Right.

Camera e mondo più grande dello schermo

Classe Camera con x,y che segue il player con movimento morbido.

Mondo (WORLD_WIDTH, WORLD_HEIGHT) più grande della finestra.

Caricamento di level2 come immagine di background.

Metodo loadLevel(BufferedImage) che scorre i pixel dell’immagine e, per i pixel verdi (0,255,0), crea un Virus con new Virus(xx, yy, ID.Enemy, handler).

Nemico base (SpikeVirus)

Logica di movimento random presente (anche se da rifinire).

Hitbox normale (getBounds) e hitbox “grande” (getBoundsBig) già pronta per collisioni future.

Spawn “in stile Vampire Survivors”

