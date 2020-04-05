package me.crz.minecraft.itemsystem.utils;
// not used anywhere
public class Pair<L, M, R> {

	private L left;
	private M middle;
	private R right;

	public Pair() {
	}

	public Pair(L left, M middle, R right) {
		this.left = left;
		this.middle = middle;
		this.right = right;
	}

	public L getLeft() {
		return left;
	}
	
	public M getMiddle() {
		return middle;
	}

	public R getRight() {
		return right;
	}

	public void setLeft(L left) {
		this.left = left;
	}
	
	public void setMiddle(M middle) {
		this.middle = middle;
	}

	public void setRight(R right) {
		this.right = right;
	}

}