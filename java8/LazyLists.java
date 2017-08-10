package com.test;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class LazyLists {

	public static void main(String... args) {

		MyList<Integer> l = new MyLinkedList<>(5, new MyLinkedList<>(10, new EmptyList<>()));
		System.out.println(l.head());

		LazyList<Integer> lazyList = from(2);
		for (int i = 0; i < 10; i++) {
			System.out.println(lazyList.head());
			lazyList = (LazyList<Integer>)lazyList.tail();
		}

		LazyList<Integer> numbers = from(2);
		System.out.println("primes");
		for (int i = 0; i < 1_000_000; i++) {
			numbers = (LazyList<Integer>)primes(numbers);
			System.out.println(numbers.head());
			numbers = (LazyList<Integer>)numbers.tail();
		}
	}

	interface MyList<T> {
		T head();

		MyList<T> tail();

		default boolean isEmpty() {
			return true;
		}

		MyList<T> filter(Predicate<T> p);
	}

	static class MyLinkedList<T> implements MyList<T> {
		final T head;
		final MyList<T> tail;

		public MyLinkedList(T head, MyList<T> tail) {
			this.head = head;
			this.tail = tail;
		}

		public T head() {
			return head;
		}

		public MyList<T> tail() {
			return tail;
		}

		public boolean isEmpty() {
			return false;
		}

		public MyList<T> filter(Predicate<T> p) {
			return isEmpty() ? this : p.test(head()) ? 
					new MyLinkedList(head(), tail().filter(p)) : tail().filter(p);
		}
	}

	static class EmptyList<T> implements MyList<T> {
		public T head() {
			throw new UnsupportedOperationException();
		}

		public MyList<T> tail() {
			throw new UnsupportedOperationException();
		}

		public MyList<T> filter(Predicate<T> p) {
			return this;
		}

	}

	static class LazyList<T> implements MyList<T> {
		final T head;
		final Supplier<MyList<T>> tail;

		public LazyList(T head, Supplier<MyList<T>> tail) {
			this.head = head;
			this.tail = tail;
		}

		public T head() {
			return head;
		}

		public MyList<T> tail() {
			return tail.get();
		}

		public boolean isEmpty() {
			return false;
		}

		public MyList<T> filter(Predicate<T> p) {
			return isEmpty() ? this : p.test(head()) ?
					new LazyList<T>(head(), () -> tail().filter(p)) : tail().filter(p);
		}
	}

	private static LazyList<Integer> from(Integer n) {
		return new LazyList<Integer>(n, () -> from(n+1));
	}

	private static MyList<Integer> primes(MyList<Integer> numbers) {
		return new LazyList<Integer>(numbers.head(), () -> numbers.tail().filter(n -> n % numbers.head() != 0));
	}
}
