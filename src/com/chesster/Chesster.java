package com.chesster;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Класс реализует шахматную доску и пул фигур, которые будут разполагаться на
 * ней
 * 
 * @author Const Hrim
 */
public class Chesster {
	/**
	 * Класс реализует шахматную доску и пул фигур, которые будут разполагаться
	 * на ней
	 */

	/** Размерность шахматной доски */
	public static final byte DIM = 2;
	JFrame frame;
	/** ячейки шахматной доски */
	final JPanel squares[][] = new JPanel[DIM][DIM];
	/** пул объектов созданых на доске */
	private List<Rook> componentlist = Collections
			.synchronizedList(new ArrayList<Rook>());

	/**
	 * Метод создает окно 500х500, отрисовывает доску. Создает ладьи и сохраняет
	 * их в пул объектов
	 * 
	 * @param rooksCount
	 *            - количество объектов {@link Rook} которые создаются на доске.
	 * @param iterations
	 *            - кол-во шагов каждой фигуры
	 */
	public Chesster() {
		frame = new JFrame("Simplified Chess");
		frame.setSize(500, 500);
		frame.setLayout(new GridLayout(DIM, DIM));
		// Отрисовываем ячейки шахматной доски
		for (byte i = 0; i < DIM; i++) {
			for (byte j = 0; j < DIM; j++) {
				squares[i][j] = new JPanel();

				if ((i + j) % 2 == 0) {
					squares[i][j].setBackground(Color.black);
				} else {
					squares[i][j].setBackground(Color.white);
				}
				frame.add(squares[i][j]);
			}
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * Метод создает окно 500х500, отрисовывает доску. Создает ладьи и сохраняет
	 * их в пул объектов
	 * 
	 * @param rooksCount
	 *            - количество объектов {@link Rook} которые создаются на доске.
	 * @param iterations
	 *            - кол-во шагов каждой фигуры
	 */
	public void ChessterInit(int rooksCount, int iterations) {
		// Создаем объекты, заполняя их положение
		byte x = 0;
		byte y = 0;

		// При попытке создать объектов больше размерности
		// доски, просто отрезаем лишнее
		rooksCount = (rooksCount > (DIM * DIM)) ? (DIM * DIM) : rooksCount;

		// вычисляем положение объекта
		for (byte n = 0; n < rooksCount; n++) {
			// гарантируем что каждому объекту найдется место на доске
			do {
				x = (byte) (Math.random() * (DIM));
				y = (byte) (Math.random() * (DIM));
			} while (checkCell(n, x, y));
			// Создаем объект

			Rook rookComponent = new Rook(x, y);
			// Добавляем его в пул
			componentlist.add(rookComponent);
			// Помещаем на доску
			squares[x][y].add(rookComponent.rook);
		}
	}

	/**
	 * Проверяет совпадает ли переданная позиция на доске, объекту {@link Rook}
	 * в пуле {@link componentlist}
	 * 
	 * @param x
	 *            - позиция по вертикали
	 * @param y
	 *            - позиция по горизонтали
	 * @return true Если такая позиция существует, false иначе
	 */
	public boolean checkCell(int pos, byte x, byte y) {
		for (int i = 0; i < componentlist.size(); i++) {
			if (pos != i) {
				Rook currentRook = getRook(i);
				if ((currentRook.currentX == x) && (currentRook.currentY == y)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Меняем позицию объекта {@link Rook}
	 * 
	 * @param pos
	 *            - позиция в пуле {@link componentlist}
	 */
	public void changeRookPosition(int pos) {
		Rook currRook = componentlist.get(pos);
		synchronized (squares[currRook.currentX][currRook.currentY]) {
			// Запоминаем старые позиции
			byte oldX = currRook.currentX;
			byte oldY = currRook.currentY;
			// Если кто-то хочет удалить из этой же ячейки
			byte[] noskip = takeNewPosition(currRook);
			if (noskip != null) {
				synchronized (squares[noskip[0]][noskip[1]]) {
					// попробуем встать на ячейку 2 раза с ожиданием в 2000мс.
					// Если не получится пропустим ход.
					byte tries = 0;
					while (checkCell(pos, noskip[0], noskip[1])) {
						if (tries > 2)
							break;
						tries++;
						try {
							squares[noskip[0]][noskip[1]].wait(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					// Если не получилось переместиться просто пропускаем ход
					if (tries > 2)
						return;
					// Убираем из старой
					squares[currRook.currentX][currRook.currentY]
							.remove(currRook.rook);
					squares[currRook.currentX][currRook.currentY].validate();
					squares[currRook.currentX][currRook.currentY].repaint();
					// Рисуем в новой ячейке
					currRook.currentX = noskip[0];
					currRook.currentY = noskip[1];
					squares[currRook.currentX][currRook.currentY]
							.add(currRook.rook);
					squares[currRook.currentX][currRook.currentY].validate();
					squares[currRook.currentX][currRook.currentY].repaint();

				}
				squares[oldX][oldY].notifyAll();
			}
		}
	}

	/**
	 *  Метод вычисляет новое положение для фигуры и возращает его ввиде массива
	 *  
	 * @param currentRook - фигура {@link Rook}
	 * @return - Массив byte [0] - положение фигуры по Ох, [1] - положение фигуры по Оy
	 */
	private byte[] takeNewPosition(Rook currentRook) {
		byte[] newPos = new byte[2];
		byte newX = currentRook.currentX;
		byte newY = currentRook.currentY;
		// Если кто-то хочет удалить из этой же ячейки

		// Случайно определяем по какой оси будет производится перемещение X или
		// Y. При этом стоять на месте не интересно. Пробуем 20 раз если
		// сдвинуться не получилось - неудача
		for (int i = 0; i < 20; i++) {
			byte choose = (byte) (Math.random() + 0.5);
			// Случайно определяем направление в рамках выбранной оси
			byte direction = (byte) (Math.random() + 0.5);
			direction = (byte) ((direction == 0) ? -1 : 1);

			if (choose == 0) {
				newX = solvePosition(currentRook.currentX, direction);
			} else {
				newY = solvePosition(currentRook.currentY, direction);
			}
			if ((currentRook.currentX != newX)
					|| (currentRook.currentY != newY)) {
				newPos[0] = newX;
				newPos[1] = newY;
				return newPos;
			}
		}

		return newPos;
	}

	/**
	 * Внутренний метод вычисляет новое значение положения на прямой направления
	 * (горизонтальном - у или вертикальном - х) движения.
	 * 
	 * @param pos
	 *            - Текущее положение на прямой направления
	 * @param direction
	 *            - Направление в котором надо двигаться.
	 * @return Новое положение
	 */
	private byte solvePosition(byte pos, byte direction) {
		byte newpos = (byte) (pos + (direction * ((short) (Math.random() * ((Chesster.DIM - pos) + 1)))));
		newpos = (newpos < 0) ? 0 : newpos;
		newpos = (newpos > (DIM - 1)) ? (DIM - 1) : newpos;
		return newpos;
	}

	/**
	 * Получаем {@link Rook}
	 * 
	 * @param pos
	 *            - позиция в пуле {@link componentlist}
	 * @return объект {@link Rook} с позицией pos в пуле
	 */
	public Rook getRook(int pos) {
		return componentlist.get(pos);
	}
}
