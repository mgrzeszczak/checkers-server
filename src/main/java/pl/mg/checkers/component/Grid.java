package pl.mg.checkers.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by maciej on 25.12.15.
 */
@Component
@Scope("prototype")
public class Grid {

    public static final int SIZE = 8;
    private int[][] grid;

    @PostConstruct
    private void init(){
        grid = new int[SIZE][SIZE];
        for (int i=0;i<SIZE;i++){
            for (int j=0;j<SIZE;j++){
                grid[i][j]=0;
            }
        }
        for (int i=0;i<3;i++){
            for (int j=0;j<4;j++){
                grid[2*j+1-i%2][i] = 1;
                grid[2*j+i%2][i+5] = 2;
            }
        }
    }

    public void print(){
        for (int i=0;i<SIZE;i++){
            for (int j=0;j<SIZE;j++){
                System.out.print(grid[j][i]+"  ");
            }
            System.out.println();
        }
    }

    public int[][] getGrid(){
        return grid;
    }
}
