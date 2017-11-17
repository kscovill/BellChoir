import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Player implements Runnable {
	public boolean timeToPlay = false;
	public boolean timeToRun = false;
	public AudioFormat af;
	public List<BellNote> length;
	public Note newNote;

	public final Thread thread;

	/**
	 * Constructor for each player
	 * Must be passed the song, their note, and the audioFormat
	 * @param list
	 * @param n
	 * @param a
	 */
	Player(List<BellNote> list, Note n, AudioFormat a) {
		thread = new Thread(this, "Musician " + n);
		this.af = a;
		this.length = list;
		this.timeToRun = true;
		this.newNote = n;
		thread.start();
		System.out.println(thread.getName() + " is Running");
	}

	/**
	 * Attempts to gain access and if its not time to play
	 * then they wait.
	 * @throws InterruptedException
	 */
	public synchronized void gainAccess() throws InterruptedException {
		if (!timeToPlay) {
			wait();
		}
	}

	/**
	 * makes it not time to play and notifies everyone
	 */
	public synchronized void release() {
		timeToPlay = false;
		notify();
	}

	/**
	 * runs when the thread starts
	 * while it is time to run, it checks if it is time to play
	 * if it is then it makes sure
	 * then it plays the song
	 * then releases their resource
	 */
	@Override
	public void run() {
		while (timeToRun) {
			try {
				gainAccess();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (timeToPlay) {
				nowPlay();
				release();
			}

		}

	}

	/**
	 * Start the thread by the timeToRun = true
	 */
	public void startRunning() {
		timeToRun = true;
	}

	/**
	 * Makes it not time to run
	 */
	public void stopRunning() {
		timeToRun = false;
		release();
	}

	/**
	 * play method for the constructor to call
	 */
	public synchronized void play() {
		timeToPlay = true;
		notify();
		while (timeToPlay) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	public void waitToStop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			System.err.println(thread.getName() + " stop malfunction");
		}
	}

	public synchronized void nowPlay() {
		System.out.println(thread.getName() + " is trying to play");
		try (final SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
			line.open();
			line.start();

			playNote(line, new BellNote(newNote, length.remove(0).length));

			line.drain();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void playNote(SourceDataLine line, BellNote bn) {

		final int ms = Math.min(bn.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
		final int length = Note.SAMPLE_RATE * ms / 1000;
		line.write(bn.note.sample(), 0, length);
		line.write(Note.REST.sample(), 0, 100);
	}
}
