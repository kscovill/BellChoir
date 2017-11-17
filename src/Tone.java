import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tone Class
 * 
 * Initializes a musician for each note Acts as A conductor to call which
 * musicians to play Then stops and ends all threads(musicians)
 * 
 */
public class Tone {

	public static final int TIME_BETWEEN_NOTES = 100;
	private static boolean success = true;
	private static Player[] p;
	private static Thread thread;

	/**
	 * Main method that is run at startup
	 * 
	 * @param args
	 *            args passed is the name of the file containing the song
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		final AudioFormat af = new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
		Tone t = new Tone(af);
		loadNotes(args[0]);
		if (!success) {
			System.err.println("This is an Invalid Song");
			System.exit(1);
		}
		p = new Player[14]; // note length
		int pi =0;
		for (Note n : Note.values()) {
			p[pi] = new Player(song,n,af);
			pi++;
		}
//		Note base = Note.REST;
//		for (int i = 0; i < 14; i++) {
//			p[i] = new Player(song, base, af);
//			System.out.println(base + " is " + i);
//			if (i < 13) {
//				base = base.getNext();
//			}
//		}
		System.out.println("TRYING TO PLAY SONG");
		t.playSong(song);
		System.out.println("SONG FINISHED");

		for (int i = 0; i < 14; i++) {
			p[i].stopRunning();
			System.out.println("STOPPING " + i);
		}
		for (int i = 0; i < 14; i++) {
			p[i].waitToStop();
			System.out.println("Joining " + i);
		}
	}

	private static final List<BellNote> song = new ArrayList<BellNote>();

	private static void loadNotes(String filename) {
		final File file = new File(filename);
		if (file.exists()) {
			System.out.println("FILES EXISTS!");
			try (FileReader fileReader = new FileReader(file); BufferedReader br = new BufferedReader(fileReader)) {
				String line = null;
				while ((line = br.readLine()) != null) {
					BellNote n = parseNote(line);
					if (n != null) {
						System.out.println("ADDING LINE");
						song.add(n);
					} else {
						System.err.println("Error: Invalid note '" + line + "'");
						success = false;
					}
				}
			} catch (IOException ignored) {
			}
		} else {
			System.err.println("File '" + filename + "' not found");
		}
		// return song;
	}

	private final AudioFormat af;

	Tone(AudioFormat af) {
		this.af = af;
	}

	void playSong(List<BellNote> song) throws LineUnavailableException, InterruptedException {
		try (final SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
			line.open();
			line.start();
			while (!song.isEmpty()) {
				doTheThing(song);
			}
			// for (BellNote bn : song) {
			// playNote(line, bn);
			// }
			line.drain();
		}
	}

	private synchronized void doTheThing(List<BellNote> s) throws InterruptedException {

		Note nn = s.get(0).note;
		System.out.println(s.get(0).note);
		switch (nn) {
		case A4:
			p[1].play();
			break;
		case A4S:
			p[2].play();
			break;
		case B4:
			p[3].play();
			break;
		case C4:
			p[4].play();
			break;
		case C4S:
			p[5].play();
			break;
		case D4:
			p[6].play();
			break;
		case D4S:
			p[7].play();
			break;
		case E4:
			p[8].play();
			break;
		case F4:
			p[9].play();
			break;
		case F4S:
			p[10].play();
			break;
		case G4:
			p[11].play();
			break;
		case G4S:
			p[12].play();
			break;
		case A5:
			p[13].play();
			break;
		case REST:
			p[0].play();
			break;
		}
		// final int ms = Math.min(s.get(0).length.timeMs(), Note.MEASURE_LENGTH_SEC *
		// 1000);
		// final int newLength = Note.SAMPLE_RATE * ms / 1000;
		// Thread.sleep(s.get(0).length.timeMs()+100);

	}

	private synchronized void playNote(SourceDataLine line, BellNote bn) {
		final int ms = Math.min(bn.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
		final int length = Note.SAMPLE_RATE * ms / 1000;
		line.write(bn.note.sample(), 0, length);
		line.write(Note.REST.sample(), 0, 100);
	}

	private static BellNote parseNote(String line) {
		String[] fields = line.split("\\s+");

		if (fields.length == 2) {

			Note newNote = null;
			int newInt = parseInt(fields[1]);
			System.out.println(fields[0]);

			switch (fields[0]) {

			case "A4":
				newNote = Note.A4;
				break;
			case "A4S":
				newNote = Note.A4S;
				break;
			case "B4":
				newNote = Note.B4;
				break;
			case "C4":
				newNote = Note.C4;
				break;
			case "C4S":
				newNote = Note.C4S;
				break;
			case "D4":
				newNote = Note.D4;
				break;
			case "D4S":
				newNote = Note.D4S;
				break;
			case "E4":
				newNote = Note.E4;
				break;
			case "F4":
				newNote = Note.F4;
				break;
			case "F4S":
				newNote = Note.F4S;
				break;
			case "G4":
				newNote = Note.G4;
				break;
			case "G4S":
				newNote = Note.G4S;
				break;
			case "A5":
				newNote = Note.A5;
				break;
			case "REST":
				newNote = Note.REST;
				break;
			default:
				break;

			}
			if (newNote != null) {

				switch (newInt) {
				case 8:
					System.out.println(newNote);
					return new BellNote(newNote, NoteLength.EIGHTH);
				case 4:
					System.out.println(newNote);
					return new BellNote(newNote, NoteLength.QUARTER);
				// length = NoteLength.QUARTER;
				case 2:
					System.out.println(newNote);
					return new BellNote(newNote, NoteLength.HALF);
				// length = NoteLength.HALF;
				case 1:
					System.out.println(newNote);
					return new BellNote(newNote, NoteLength.WHOLE);
				// length = NoteLength.WHOLE;
				default:
					break;
				}
				// return new BellNote(newNote,length);
			}
		}
		return null;
	}

	private static int parseInt(String num) {
		try {
			return Integer.parseInt(num);
		} catch (NumberFormatException ignored) {
		}
		return 0;
	}

}

class BellNote {
	final Note note;
	final NoteLength length;

	BellNote(Note note, NoteLength length) {
		this.note = note;
		this.length = length;
	}
}

enum NoteLength {
	WHOLE(1.0f), HALF(0.5f), QUARTER(0.25f), EIGHTH(0.125f);

	private final int timeMs;

	private NoteLength(float length) {
		timeMs = (int) (length * Note.MEASURE_LENGTH_SEC * 1000);
	}

	public int timeMs() {
		return timeMs;
	}
}

enum Note {
	// REST Must be the first 'Note'
	REST, A4, A4S, B4, C4, C4S, D4, D4S, E4, F4, F4S, G4, G4S, A5;

	public static final int SAMPLE_RATE = 48 * 1024; // ~48KHz
	public static final int MEASURE_LENGTH_SEC = 1;

	// Circumference of a circle divided by # of samples
	private static final double step_alpha = (2.0d * Math.PI) / SAMPLE_RATE;
	private static final int finalIndex = Note.values().length - 1;

	Note getNext() {
		int currIndex = this.ordinal();
		if (currIndex >= finalIndex) {
			throw new IllegalStateException("Already at final state");
		}
		return Note.values()[currIndex + 1];
	}

	private final double FREQUENCY_A_HZ = 440.0d;
	private final double MAX_VOLUME = 127.0d;

	private final byte[] sinSample = new byte[MEASURE_LENGTH_SEC * SAMPLE_RATE];

	private Note() {
		int n = this.ordinal();
		if (n > 0) {
			// Calculate the frequency!
			final double halfStepUpFromA = n - 1;
			final double exp = halfStepUpFromA / 12.0d;
			final double freq = FREQUENCY_A_HZ * Math.pow(2.0d, exp);

			// Create sinusoidal data sample for the desired frequency
			final double sinStep = freq * step_alpha;
			for (int i = 0; i < sinSample.length; i++) {
				sinSample[i] = (byte) (Math.sin(i * sinStep) * MAX_VOLUME);
			}
		}
	}

	public byte[] sample() {
		return sinSample;
	}

}