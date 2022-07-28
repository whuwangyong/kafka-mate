import list_topics
import delete_topics

if __name__ == "__main__":
    topics = list_topics.list_topics()
    for t in topics:
        delete_topics.delete_topic(t)
