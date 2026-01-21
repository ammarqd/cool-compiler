class A {
    self : SELF_TYPE <- 5;
    foo(self : Int) : Int {
        let self : SELF_TYPE <- 5 in z
    };
};

class B inherits A {
    foo() : Int {
        2
    };
};