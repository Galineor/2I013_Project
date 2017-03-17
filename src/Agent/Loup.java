package Agent;

import Environnement.*;

public class Loup extends Pred {
	
	private boolean isChasing;
	public final int ageAdulte = 40;
	public final int ageVieux = 100;

	public Loup(Map world) {
		this(world, (int)(Math.random()*world.getWidth()),(int)(Math.random()*world.getHeight()) );
	}
	
	public Loup(Map world, int x, int y) {
		super(world, 100, 150);
		this.posX = x;
		this.posY = y;
		this.direction = (int)(Math.random()*4);
		this.rt = reprodTime;
		this.ht = hungerTime;
		this.directionPrec = -1;
		this.isChasing = false;
		this.parent = null;
		this.prevPosX = -1;
		this.prevPosY = -1;
		this.age = 0;
	}
	
	public boolean manger(){
		for(Agent a : world.getAgents()){
			if(!a.isPred && a.isAlive() && a.getPosX() == this.posX && a.getPosY() == this.posY){
				a.setAlive(false);
				return true;
			}
		}
		return false;
	}
	
	public void reproduire(){
		for(Agent a : world.getAgents()){
			if(a.isPred && !a.isAlive){
				a.setAlive(true);
				a.setPosX(this.posX);
				a.setPosY(this.posY);
				((Pred)a).setRt(reprodTime);
				((Pred)a).setHt(hungerTime);
				a.directionPrec = -1;
				((Loup)a).isChasing = false;
				a.setAge(0);
				a.setParent(this);
				a.setPrevPosX(-1);
				a.setPrevPosY(-1);
				return;
			}
		}	
		(world).getToAdd().add(new Loup(world, this.posX, this.posY));
	}
		
	public void chasser(){
		for (Agent a : world.getAgents()){
			if(!a.isPred && a.isAlive()){
				if(a.posY >= this.posY - 4 && a.posY<=this.posY && a.posX == this.posX){
					this.direction = 0;
					isChasing = true;
				}
				if(a.posX <= this.posX + 4 && a.posX>=this.posX && a.posY == this.posY){
					this.direction = 1;
					isChasing = true;
				}
				if(a.posY <= this.posY + 4 && a.posY>=this.posY && a.posX == this.posX){
					this.direction = 2;			
					isChasing = true;
				}
				if(a.posX >= this.posX - 4 && a.posX<=this.posX && a.posY == this.posY){
					this.direction = 3;
					isChasing = true;
				}
			}
		}
	}
	
	
	@Override
	public void Step() {
		 updatePrevPos();
		//Si le loup a trop faim, il meurt
		if(ht <= 0){
			this.setAlive(false);
			return;
		}
		if(age<40){
			comportementJeune();
		}else{
			comportementAdulte();
		}
		age++;
		 this.directionPrec = this.direction;
	}
	

	
	@Override
	public void comportementJeune() {
		// TODO Auto-generated method stub
		
		/* 
		 * Suit le parent
		 * Pas necessaire de manger
		 * Pas de reproduction
		 * Endurance forte
		 */
		if(parent != null && parent.isAlive()){
			this.setPosX(parent.getPosX());
			this.setPosY(parent.getPosY());
		}else{
			age = ageAdulte;
			comportementAdulte();
		}
		
		
	}

	@Override
	public void comportementAdulte() {
		// TODO Auto-generated method stub
		/*
		 * Chasse
		 * Reproduction
		 * Comportement en meute
		 * Endurance normale
		 */
		
		//On mange avant de se deplacer
		if(manger()){
			ht = hungerTime;
		}

		
		//Le loup se reproduit apres reprodTime iterations
		if(rt == 0){
			reproduire();
			rt = reprodTime;
		}

		
		isChasing = false; //A chaque debut de tour, on ne sait pas si le loup est entrain de chasser
		chasser();
		//Permet d'�viter les deplacements avant/arriere en boucle, rendant la simulation plus realiste
		if(!isChasing && directionPrec>0 && direction == (directionPrec+2)%4){
			if ( Math.random() > 0.5 ){ // au hasard
				for(int i=0; i<3; i++){
					direction = (direction+1) %4;
					if(!isWaterDirection(direction)){
						break;
					}
				}
			}
			else{
				for(int i=0; i<3; i++){
					direction = (direction-1+4) %4;
					if(!isWaterDirection(direction)){
						break;
					}
				}
			}
		}
		
		//Si le loup ne peut pas se deplacer dans la direction actuelle, on essaie les autres directions
		if(isWaterDirection(direction)){
			if ( Math.random() > 0.5 ){ // au hasard
				for(int i=0; i<3; i++){
					direction = (direction+1) %4;
					if(!isWaterDirection(direction)){
						break;
					}
				}
			}
			else{
				for(int i=0; i<3; i++){
					direction = (direction-1+4) %4;
					if(!isWaterDirection(direction)){
						break;
					}
				}
			}	
		}
		
		// met a jour: la position de l'agent (depend de l'orientation)
		if(!isWaterDirection(direction)){
			 switch ( direction ) 
			 {
	         	case 0: // nord
	         		posY = ( posY - 1 + world.getHeight() ) % world.getHeight();
	         		break;
	         	case 1:	// est
	         		posX = ( posX + 1 + world.getWidth() ) % world.getWidth();
	 				break;
	         	case 2:	// sud
	         		posY = ( posY + 1 + world.getHeight() ) % world.getHeight();
	 				break;
	         	case 3:	// ouest
	         		posX = ( posX - 1 + world.getWidth() ) % world.getWidth();
	 				break;
	 			default:
	 				System.out.println("PROBLEME");
			 }
		}
		
		
		 if(manger()){
			ht = hungerTime;
		}
		 
		 ht--;
		 rt--;
		 
		 this.directionPrec = this.direction;
}


	@Override
	public void comportementVieux() {
		// TODO Auto-generated method stub
		
		/*
		 * Moins d'endurance
		 * Plus petit champ de vision
		 * 
		 */
	}
}
