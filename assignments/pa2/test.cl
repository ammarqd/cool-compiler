class A {
    self : SELF_TYPE <- blah;
    foo(self : Int, self : Int) : Int {
        self <- 5
    };
};

class B inherits A {
    foo() : Int {
        2
    };
};

