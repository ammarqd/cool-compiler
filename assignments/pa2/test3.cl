class Main {
    -- Test attribute initialization type mismatch
    attr1 : Int <- "string";           -- String doesn't conform to Int
    attr2 : Bool <- 42;                -- Int doesn't conform to Bool

    main() : Object {
        {
            -- Test assignment type mismatch
            let x : Int <- 0 in {
                x <- "hello";          -- String doesn't conform to Int
                x                      -- Should still be treated as String type (RHS)
            };

            -- Test let initialization type mismatch
            let y : Bool <- 5 in       -- Int doesn't conform to Bool
                y;                     -- Body has valid type Bool

            -- Test method return type mismatch
            test_method();

            0;
        }
    };

    -- Method with return type mismatch
    test_method() : Int {
        "I return a string"            -- String doesn't conform to Int
    };

    -- Another method to test cascading
    use_test_method() : Int {
        test_method() + 5              -- If test_method returns Object instead of String,
                                       -- this might give wrong error about Object + Int
    };
};