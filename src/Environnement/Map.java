package Environnement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import Agent.Agent;

public class Map {
	
	private Terrain[][] terrain;
	private final int dx, dy;
	protected int water [][];
	private ArrayList<Agent> agents;
	public ArrayList<Agent> toAdd = new ArrayList<Agent>();
	
	private final static double OCEAN = 0.3;
	
	public Map(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
		
		terrain = new Terrain[dx][dy];

		agents = new ArrayList<Agent>();		
		boolean tree;
		
		for (int x = 0; x < terrain.length; x++) {
			for (int y = 0; y < terrain.length; y++) {
				if (Math.random() < 0.2)
					tree = true;
				else
					tree = false;
					
				//initialisation du terrain
				if(Math.random()<(1-(x*OCEAN)) ||Math.random()<(1-(y*OCEAN))
						||Math.random()<(1-((dx-x-1)*OCEAN)) ||Math.random()<(1-((dy-y-1)*OCEAN))){
						//ajout de la mer autour de l ile
						terrain[x][y] = new Terrain (2, 2, 0, false);
				}else{
					if (x > terrain.length/2 && y > terrain.length/2){
						//cree une case de desert
						terrain[x][y] = new Terrain(1, 0, 0, tree);
					
					}else{
						if (x > terrain.length/2 - 2 && y > terrain.length/2 - 2){
							//cree une transition entre le desert et la foret
							terrain[x][y] = new Terrain ((int)(Math.random() * 2), 0 ,0 , false);
						}else{
							if (Math.random() < 0.01){
								//initialisation de la foret avec de l eau
								terrain[x][y] = new Terrain (2, (int)(Math.random() * 5), 0, false);
							}else{
								//initialisation foret
								terrain[x][y] = new Terrain(0, 0, 0, tree);
							}
						}
					}
				}
			}
		}
	}

	public void Step (){
		StepWorld();
		StepAgent();
	}
	
	public void StepAgent(){
		for(Agent a : getAgents()){
			if (a.isAlive() && Math.random() < 0.80)
				a.Step();
			else
				a.updatePrevPos(); // Si on le met pas a jour, on modifie sa position precedente ( permet l'affichage d'agents immobile )
		}
		
		agents.addAll(toAdd);
		toAdd.clear();
	}
	
	public void StepWorld(){
		majForet(terrain);
		majEau(terrain);
	}
	
	public void majForet (Terrain[][] t){
		int x, y;
		for(int i = 0; i < (dx*dy)/3 ; i++){
			x = (int)(Math.random() * dx);
			y = (int)(Math.random() * dy);
			if (t[x][y].type == 0){
				//verifie si la case est bien un arbre
				if(!t[x][y].isTree){
					//fait pousser un arbre avec chance plus elevee si il y a des cendres
					if (t[x][y].getAFA()==2 && Math.random() < 0.1){
						t[x][y].isTree = true;
						t[x][y].setAFA(0);
					}else{
						if(Math.random()<0.0005 && t[x][y].getAFA() != 2){
							t[x][y].isTree = true;
							t[x][y].setAFA(0);
						}	
					}
					if (Math.random() < 0.05 && t[x][y].getAFA() == 2)
						t[x][y].setAFA(0);
					
				}else{
					//les arbres en feu deviennent des cendres
					if (t[x][y].getAFA() == 1){
						t[x][y].setAFA(2);
						t[x][y].isTree = false;
					}
					//propagation du feu
					if (t[x][y].getAFA() == 0 && (t[(x-1+dx)%dx][y].getAFA() == 1 ||t[(x+1+dx)%dx][y].getAFA() == 1 
							||t[x][(y-1+dy)%dy].getAFA() == 1 ||t[x][(y+1+dy)%dy].getAFA() == 1)){
						t[x][y].setAFA(1);
					}
					
					if (Math.random() < 0.0001){
						t[x][y].setAFA(1);
					}
				}
			}
		}
	}
	
	public void majEau (Terrain[][] t){
		for(int x = 0; x < dx; x++){
			for (int y = 0; y < dy; y++){
				if (!t[x][y].isTree){
					if (t[(x - 1 + dx)% dx][y].water > t[x][y].water && t[(x - 1 + dx)% dx][y].water > 1){
						t[x][y].water ++;
						t[(x - 1 + dx)% dx][y].water --;
						t[x][y].type = 2;
						
					}if (t[(x + 1)% dx][y].water > t[x][y].water && t[(x + 1)% dx][y].water > 1){
						t[x][y].water ++;
						t[(x + 1)% dx][y].water --;
						t[x][y].type = 2;
						
					}if(t[x][(y - 1 + dy)% dy].water > t[x][y].water && t[x][(y - 1 + dx)% dx].water > 1){
						t[x][y].water ++;
						t[x][(y - 1 + dy)% dy].water --;
						t[x][y].type = 2;
					
					}if(t[x][(y + 1)% dy].water > t[x][y].water && t[x][(y + 1)% dy].water > 1){
						t[x][y].water ++;
						t[x][(y + 1)% dy].water --;
						t[x][y].type = 2;
					}
				}
			}
		}
	}
	
	public Terrain[][] getTerrain(){
		return this.terrain;
	}
	
	public int[][] getWater(){
		return this.water;
	}
	
	public int getHeight(){
		return this.dy;
	}
	
	public int getWidth(){
		return this.dx;
	}
	
	public ArrayList<Agent> getAgents(){
		return this.agents;
	}
}
