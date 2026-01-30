package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Adding a song."

    request {
        url "/songs"
        method POST()
        headers {
            contentType(applicationJson())
        }
        body(
                name: "name",
                artist: "artist",
                album: "album",
                length: "length",
                resourceId: "1",
                year: "year"
        )
    }

    response {
        status 200
        headers {
            contentType(applicationXml())
        }
        body([id: 1L])
    }
}