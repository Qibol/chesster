package com.chesster;

import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * Основной исполняемый класс.
 * 
 * На шахматной доске находятся в произвольной позиции N ладей (4-6) Они все
 * одновременно начинают ходить на случайные позиции (при этом перемещаться они,
 * естественно, могут только горизонтально либо вертикально). Между каждыми 2
 * ходами каждая фигура делает паузу 200-300 миллисекунд. Если на пути фигуры
 * оказывается другая, она ждет, пока путь освободится. Если в течение 5 секунд
 * проход не освободился, выбирается другая позиция аналогичным случайным
 * образом. Все заканчивается, когда все фигуры сделают по 50 ходов
 * 
 * @author Const Hrim
 */

public class Executor {

	/** кол-во фигур по умолчанию */
	static int count = 5;
	/** кол-во итераций по умолчанию (сколько шагов делает фигура) */
	static int iter = 60;

	/**
	 * Основной метод. Можно выполнить через консоль, передав значения кол-ва
	 * фигур и итераций через параметры запуска. Первый параметр кол-во фигур,
	 * второй ко-во итераций
	 * 
	 */
	public static void main(String[] args) throws InterruptedException {
		int rooksCount = count;
		int iterations = iter;
		int cellcount = Chesster.DIM * Chesster.DIM;

		// Из консольки
		if (args.length > 0) {
			rooksCount = args[0].isEmpty() ? rooksCount : Integer
					.parseInt(args[0]);
		}
		if (args.length > 1) {
			iterations = args[1].isEmpty() ? iterations : Integer
					.parseInt(args[1]);
		}

		// Из интерфейса
		String rooksCountString = JOptionPane.showInputDialog("Сколько фигур?");
		String iterationsString = JOptionPane
				.showInputDialog("Сколько шагов делает каждая фигура?");

		rooksCount = (rooksCountString.isEmpty()) ? rooksCount : Integer
				.parseInt(rooksCountString);
		iterations = (iterationsString.isEmpty()) ? iterations : Integer
				.parseInt(iterationsString);

		// Если пытаемся добавить фигур больше чем клеток, то добавится
		// максимально возможное кол-во фигур
		rooksCount = (rooksCount > cellcount) ? cellcount : rooksCount;

		Chesster ch = new Chesster();
		ch.ChessterInit(rooksCount, iterations);

		// Создаем поток выполнения по кол-ву фигур:
		ArrayList<RookThread> rookPool = new ArrayList<RookThread>();
		for (byte k = 0; k < rooksCount; k++) {
			rookPool.add(new RookThread(ch, k, iterations));
			System.out.println("Create stream name: " + rookPool.get(k).getName());
		}

		// отдельно запустив все потоки
		for (int k = 0; k < rooksCount; k++) {
			rookPool.get(k).start();
		}

		// и подождем пока все выполнятся
		for (int k = 0; k < rooksCount; k++) {
			rookPool.get(k).join();
		}
		System.out.println("end");
	}
}
