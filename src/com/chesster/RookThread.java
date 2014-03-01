package com.chesster;

import javax.swing.JPanel;

/**
 * Класс реализует поток, который пемещает фигуры. Имеет ссылку на фигуру,
 * которой управляет.*
 * 
 * @author Const Hrim
 */
public class RookThread extends Thread {

	/** ссылка на шахматную доску */
	volatile Chesster ch;
	/** позиция в пуле объектов доски */
	byte pos;
	/** кол-во перемещений которое должен совершить поток */
	int iterations;

	/**
	 * Конструктор потока для перемещения одной фигуры
	 * 
	 * @param tch
	 *            - шахматная доска, по которой будет перемещаться фигура
	 * @param tpos
	 * @param ttime
	 *            - кол-во перемещений фигуры
	 */
	public RookThread(Chesster tch, byte tpos, int ttime) {
		ch = tch;
		pos = tpos;
		iterations = ttime;
	}

	/**
	 * Основной метод потока
	 */
	public void run() {
		if (!Thread.interrupted()) // Проверка прерывания
		{
			for (int i = 0; i < iterations; i++) {
				try {
					Thread.sleep(300);
					ch.changeRookPosition(pos);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println(Thread.currentThread().getName()+" end all iterations.");
			return;
		} else
			return;
	}
}
