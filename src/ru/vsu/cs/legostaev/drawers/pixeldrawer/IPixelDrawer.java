/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ru.vsu.cs.legostaev.drawers.pixeldrawer;

import java.awt.*;
import java.io.IOException;

public interface IPixelDrawer {
    
    void putPixel(double x, double y);
    
    void setColor(Color color);
    
    Color getColor();
       
}
