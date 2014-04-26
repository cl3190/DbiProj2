
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.*;
import java.io.*;

class Pair {
	public int key;
	public int payload;
	public Pair(int k, int p) {
		this.key = k;
		this.payload = p;
	}
	public Pair(Pair p) {
		this.key = p.key;
		this.payload = p.payload;
	}
}


public class SplashTable {
	private int B, R, S, h, bucketNum;
	private int N = 0;		//current number of records
	private int tableSize;
	private ArrayList<LinkedList<Pair>> table;
	private int[] hashM;
	
	public SplashTable(int B, int R, int S, int h) {
		this.B = B;		//buckets size
		this.R = R;		//number of reinsertions allowed
		this.S = S;		//table size, 2 to the power s
		this.h = h;		//number of hash functions
		this.tableSize = (int) Math.pow(2, S);
		this.bucketNum = this.tableSize/B;	//bucket size
		initSplash();			//initialize table
		//this.table = new int[bucketNum][this.B];		//initialize table with 0s
	}
	
	//initialize table
	private void initSplash() {
		this.table = new ArrayList<LinkedList<Pair>>(this.bucketNum);
		for (int i=0;i<this.bucketNum;i++) {
			this.table.add(new LinkedList<Pair>());
		}
		
		//generate h M values for h hash functions
		generateHashM(this.h);
	}
	
	//generate h M values for the hash function
	private void generateHashM(int h) {
		this.hashM = new int[h];
		
		for (int i=0;i<h;i++) {
			Random rd = new Random();
			int tmp = rd.nextInt();
			this.hashM[i] = (tmp % 2) == 0? tmp-1 : tmp;		//random odd number
		}
	}
	
	//formula int r = (k * M) % Math.pow(2, 32);
	//			return r>>(32-bucketNum);
	private int getHashCode(int K, int M) {
		int lg = (int)(Math.log10(this.bucketNum)/Math.log10(2));
		int r = (int) ( ( ((long)K * (long)M) ) % ( (long)Math.pow(2, 32)) );
		//System.out.println(Integer.toBinaryString(r));
		//r = (int)Math.abs( r / Math.pow(2, 31-lg) );
		r = r >>> (32- lg);
		//System.out.println(Integer.toBinaryString(r));
		
		return r;
	}
	
	private Set<Integer> getHashBuckets(int key) {
		Set<Integer> buckets = new HashSet<Integer>();
		for (int i=0;i<this.hashM.length;i++) {
			buckets.add(getHashCode(key, this.hashM[i]));
		}
		//System.out.println(buckets);
		return buckets;
	}
	
	//count available spots for a bucket
	private int getCount(int bucketNum) {
		int maxAvail = this.B - this.table.get(bucketNum).size();
		//System.out.println("max avail: "+maxAvail);
		return maxAvail;
	}
	
	//get the max availability
	private int maxAvail(Set<Integer> hashCodes) {
		int max = 0;
		for (int bucketNum: hashCodes) {
			int tmp = getCount(bucketNum);
			if (tmp > max)
				max = tmp;
		}
		return max;
	}
	
	//randomly choose a insert
	private int insertBucketNum(Set<Integer> hashCodes) {
		//System.out.println(hashCodes);
		ArrayList<Integer> candidates = new ArrayList<Integer>();
		int max = maxAvail(hashCodes);			//get the bucket number with max free slots
		//System.out.println("max: "+max);
		for (int hc:hashCodes) {
			if (getCount(hc) == max)
				candidates.add(hc);
		}
		//System.out.println("candidate buckets: " + candidates);
		//ArrayList<Integer> hc = new ArrayList<Integer>(candidates.size());
		//for (int h:hashCodes)
			//hc.add(h);
		
		if (candidates.size() <= 1)
			//return hc.get(0);
			return candidates.get(0);
		
		Random rd = new Random();
		int randomBucket = Math.abs(rd.nextInt() % candidates.size());
		//System.out.println("Random: " + randomBucket);
		//return hc.get( randomBucket );		
		return candidates.get( randomBucket );
	}
	
	//directly insert the key it is already exists
	private boolean putKeyIfExist(int key, int payload) {
		// get candidate buckets
		Set<Integer> buckets = getHashBuckets(key);
		//System.out.println(buckets);
		for (int b:buckets) {
			for (Pair p:this.table.get(b)) {
				// if the key exists, insert the value by modify the existing value, then return
				//System.out.println(p.key);
				if (key == p.key) {
					p.payload = payload;
					return true;
				}
			}
		}
		return false;
	}
	
	//insertion
	public boolean build (int key, int payload) {
		//check if exists in the table, if so, insert key and return 
		if (putKeyIfExist(key, payload))
			return true;
		
		//if the table is full, insertion fails
		if (this.N >= this.tableSize){
			return false;
		}
		
		boolean flag = false;
		Pair removed = new Pair(key, payload);
		int reinsertNum = 0;
		int insertedBucket = 0;
		
		while (!flag && reinsertNum < this.R) {
			Set<Integer> hashCodes = new HashSet<Integer>(getHashBuckets(removed.key));
			
			//don't choose the bucket that the key just be removed from
			//this only happens when candidate buckets is larger than one, and it is not the first intersion
			if (hashCodes.size() > 1 && reinsertNum != 0)	
				hashCodes.remove(insertedBucket);
			
			insertedBucket = insertBucketNum(hashCodes);
			//System.out.println("Insert into " + insertedBucket);
			
			//if bucket not full
			if (this.table.get(insertedBucket).size() < this.B) {
				this.table.get(insertedBucket).add(removed);
				this.N++;
				//System.out.println("insert into bucket " + insertedBucket);
				flag = true;
			} 
			//else bucket is full, need to initiate re-insertions
			else {
				Pair tmp = new Pair(removed);
				removed = this.table.get(insertedBucket).removeFirst();	//the removed key, need to be re-inserted
				this.table.get(insertedBucket).add(tmp);
				//flag = false;
				reinsertNum++;
			}
		}
		
		return flag;
	}
	
	public int probe(int key) {
		//get candidate buckets
		Set<Integer> buckets = getHashBuckets(key);		//must be less than or equal to h
		int payload = 0;
		for (int bucket:buckets)	//loop up to h times
			for (Pair p:this.table.get(bucket)) {	//loop up to B times
				//mask equals all 1s if it finds a matching key, else equals 0
				int mask = (p.key == key) ? 0xFFFFFFFF : 0;
				//use p.payload & mask give us the value of p.payload or 0
				int tmp = p.payload & mask;
				//payload | tmp give us the non-zero payload value
				payload = payload | tmp;
			}
		
		return payload;
	}
	
	public void printResult(int key, int payload) {
		if (payload == 0)
			return;
		System.out.println(key + " " + payload);
	}
	/*
	public void probe(int k) {
		//get candidate buckets
		Set<Integer> buckets = getHashBuckets(k);		//must be less than or equal to h
		int key = 0;					
		int payload = 0;			
		int tmpKey = 0;
		int tmpPayload = 0;
		for (int bucket:buckets)	//loop up to h times
			for (Pair p:this.table.get(bucket)) {	//loop up to B times
				tmpKey = key;
				tmpPayload = payload;		//store previous payload
				key = (k == p.key) ? p.key : tmpKey;
				payload = (k == p.key) ? p.payload : tmpPayload;
			}
		
		print(key, payload);
	}
	
	public void printTable() {
		for (LinkedList<Pair> pairs:this.table)
			for (Pair p:pairs) {
				System.out.println(p.key + " " + p.payload);
			}
	}
	*/
	
	//dump current status of the table
	public String dumpfile() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.B + " " + this.S + " " + this.h + " " + this.N + "\n");
		for (int i=0;i<this.hashM.length;i++)
			sb.append(this.hashM[i] + " ");
		sb.append("\n");
		for (LinkedList<Pair> pairs:this.table) {
			//fill empty keys with 0, and 0 payload
			while (pairs.size() < this.B)
				pairs.add(new Pair(0,0));
			for (Pair p:pairs)
				sb.append(p.key + " " + p.payload + "\n");
		}
		return sb.toString();
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		//B R S h
		//SplashTable st = new SplashTable(2, 1000, 10, 2);
		int B = Integer.parseInt(args[0]);
		int R = Integer.parseInt(args[1]);
		int S = Integer.parseInt(args[2]);
		int h = Integer.parseInt(args[3]);
		SplashTable st = new SplashTable(B, R, S, h);
		/*
		int key = 0;
		int payload = 0;
		boolean flag = true;
		while (flag) {
			Random rd = new Random();
			key = rd.nextInt();
			key = key == 0 ? key+1 : key;
			payload = rd.nextInt();
			flag = st.build(key, payload);
		}
		*/

		//build splash table from inputfile until it fails or all <key, payload> are inserted successfully
		String inputfile = args[4];
		Scanner scan = new Scanner(new File(inputfile));
		boolean flag = true;
		while (flag && scan.hasNextLine()) {
			String[] pair = scan.nextLine().split(" ");
			int key = Integer.parseInt(pair[0]);
			int payload = Integer.parseInt(pair[1]);
			flag = st.build(key, payload);
		}
		scan.close();
		
		//length=6 means the dumpfile argument is present, thus we need to dump the splash table
		if (args.length == 6) {
			String dumpfile = args[5];
			PrintWriter pw = new PrintWriter(new File(dumpfile));
			pw.write(st.dumpfile());
			pw.close();
		}
		
		//probe the splash table
		Scanner probefile = new Scanner(System.in);
		while (probefile.hasNextLine()) {
			int key = Integer.parseInt(probefile.nextLine());
			st.printResult(key, st.probe(key));
		}
		//System.out.println("load factor: " + (double)st.N/(double)st.tableSize);

	}

}


