package com.chesster;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Класс реализует объект фигуры, её положение и картинку для отображения *
 * 
 * @author Const Hrim
 */

public class Rook {
	/**
	 * Класс реализует объект фигуры, её положение и картинку для отображения
	 */

	/** Позиция по вертикали */
	byte currentX;
	/** Позиция по голизонтали */
	byte currentY;
	/** компонент картинки */
	Component rook;

	/**
	 * Стандартный конструктор. начальное положение в верхней левой клетке
	 * 
	 */
	public Rook() {
		currentX = 0;
		currentY = 0;
		rook = new JLabel(new ImageIcon("rook.png"));
	}

	/**
	 * Конструктор создает в заданной клетке
	 * 
	 * @param x
	 *            - позиция по вертикали
	 * @param y
	 *            - позиция по горизонтали
	 * 
	 */
	public Rook(byte x, byte y) {
		currentX = x;
		currentY = y;
		rook = new JLabel(new ImageIcon("rook.png"));
	}
}
