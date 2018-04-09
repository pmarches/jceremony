graph [
        directed 1
        label "Hello, I am a graph"
        node [
                id 1
                label "a"
                checkBeforeDeparture [ 
                  name "checkThis" 
                  name "checkThat"
                 ]
        ]
        node [
                id 2
                label "b"
        ]
        edge [
                source 1
                target 2
                label "check before a"
        ]
]