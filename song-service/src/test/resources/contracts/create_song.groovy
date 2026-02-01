package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Create song"

    request {
        url "/songs"
        method POST()
        headers {
            contentType('application/json')
        }
        body(
            id: 1,
            name: "Song",
            artist: "John Doe",
            album: "Songs",
            length: "60",
            released: "2020"
        )
    }
    response {
        status OK()
        headers {
            contentType('application/json')
        }
        body(id: 1)
    }
}
