(* Basic class and method scope checking *)

(* Forward reference to C - should be valid *)
class B inherits C {
    z : Int <- 5;
    foo(x: Int): Int { x + 1 };
    bar(): String { let x : String <- "hello" in x };
};

class A {
    foo(x: Int): Int { x };
    baz(s : String): Int { 42 };
};

class C {
    foo(x: Int): Int { x * 2 };
};

(* Method name conflict in same class *)
class D {
    foo(): Int { 1 };
    foo(): String { "bad" };
};

class Main {
    main(): Object { 0 };
};