Introduces a flush entity event listener that checks whether the version of the entity is out-of-date. This is specifically checking for the case when the version has been updated from application code.

This is a common case for supporting optimistic concurrency in web applications/services, which typically have minimal server-side state and would not keep a Hibernate session open from one request to the next.

This class allows the original version passed out in one response to be used for an optimistic concurrency check in a subsequent request. For example, the version may be rendered out to an HTML form as a hidden field, and bound back to the persisted entity when the form is POSTed.

Master is currently tracking the latest hibernate 4 release. See branches for earlier versions (including Hibernate 3).
