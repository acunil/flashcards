import { PencilSimple, Trash } from "phosphor-react";
import Header from "../components/header";
import { useEffect, useState } from "react";
import SubjectForm from "../components/subjectForm";
import { useAppContext } from "../contexts";
import useUpdateSubject from "../hooks/subjects/useUpdateSubject";
import useCreateSubject from "../hooks/subjects/useCreateSubject";
import useDeleteSubject from "../hooks/subjects/useDeleteSubject";
import type { Subject } from "../types/subject";
import PageWrapper from "../components/pageWrapper";
import ContentWrapper from "../components/contentWrapper";
import BackButton from "../components/backButton";
import Heading from "../components/heading";

const SubjectsPage = () => {
  const { subjects, selectedSubjectId, setSelectedSubjectId, setSubjects } =
    useAppContext();

  const [editSubjectId, setEditSubjectId] = useState<number | null>(null);

  const { updateSubject } = useUpdateSubject();
  const { createSubject } = useCreateSubject();
  const { deleteSubject } = useDeleteSubject();

  const handleSelectSubject = (id: number) => {
    setSelectedSubjectId(id);
  };

  useEffect(() => {
    if (subjects.length > 0 && !selectedSubjectId) {
      setSelectedSubjectId(subjects[0].id);
    }
  }, [subjects, selectedSubjectId, setSelectedSubjectId]);

  const handleSaveEdit = async (
    id: number,
    updated: { name: string; frontLabel: string; backLabel: string }
  ) => {
    try {
      await updateSubject({
        id,
        name: updated.name,
        frontLabel: updated.frontLabel,
        backLabel: updated.backLabel,
      });

      // Optimistically update global state with a full Subject
      setSubjects((prev) => {
        const exists = prev.some((s) => s.id === id);
        if (exists) {
          return prev.map((s) => (s.id === id ? { ...s, ...updated } : s));
        }
        // Insert new subject with valid id + updated fields
        const newSubject: Subject = { id, ...updated };
        return [...prev, newSubject];
      });

      setEditSubjectId(null);
      console.log("Saved subject:", id, updated);
    } catch (err) {
      console.error("Failed to update subject:", err);
    }
  };

  const handleAddSubject = async (newSub: {
    name: string;
    frontLabel: string;
    backLabel: string;
  }) => {
    try {
      const created = await createSubject(newSub);

      if (created && created.id) {
        // Optimistically add new subject
        setSubjects((prev) => [...prev, created]);

        // Auto-select the newly created subject
        setSelectedSubjectId(created.id);
      }

      console.log("Added subject:", created);
    } catch (err) {
      console.error("Failed to create subject:", err);
    }
  };

  const handleDeleteSubject = async (subjectId: number) => {
    try {
      const success = await deleteSubject(subjectId);
      console.log(success);
      if (success) {
        setSubjects((prev) => {
          const newSubjects = prev.filter((s) => s.id !== subjectId);

          setSelectedSubjectId((prevSelected) => {
            if (prevSelected === subjectId) {
              return newSubjects.length > 0 ? newSubjects[0].id : null;
            }
            return prevSelected;
          });

          return newSubjects;
        });
      }

      console.log("Deleted subject:", subjectId);
    } catch (err) {
      console.error("Failed to delete subject:", err);
    }
  };

  return (
    <PageWrapper className="bg-yellow-200 min-h-screen">
      <Header />
      <ContentWrapper>
        {/* Header with Back Button */}

        <div className="flex items-center mb-6">
          {subjects.length > 0 && <BackButton />}
          <Heading>Subjects</Heading>
        </div>

        {subjects.length === 0 && (
          <p className="text-center">Create a subject to begin studying</p>
        )}

        {/* Subjects list */}
        <ul className="space-y-3">
          {subjects.map((subject) => {
            const isActive = subject.id === selectedSubjectId;
            const isEditing = editSubjectId === subject.id;

            return (
              <li
                key={subject.id}
                className="flex flex-col border-2 p-3 rounded cursor-pointer hover:bg-gray-50"
                onClick={() => !isEditing && handleSelectSubject(subject.id)}
              >
                {isEditing ? (
                  <SubjectForm
                    mode="edit"
                    subject={subject}
                    onSave={(values) => handleSaveEdit(subject.id, values)}
                    onCancel={() => setEditSubjectId(null)}
                  />
                ) : (
                  <div className="flex items-center justify-between">
                    {/* Radio indicator + labels */}
                    <div className="flex items-center gap-3">
                      <div className="w-5 h-5 rounded-full border-2 flex items-center justify-center">
                        {isActive && (
                          <div className="w-3 h-3 bg-black rounded-full"></div>
                        )}
                      </div>
                      <div>
                        <p className="font-semibold">{subject.name}</p>
                        <p className="text-xs text-gray-500">
                          {subject?.frontLabel || "Front"} |{" "}
                          {subject?.backLabel || "Back"}
                        </p>
                      </div>
                    </div>

                    {/* Edit + Delete buttons */}
                    <div className="flex flex-row gap-2">
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          setEditSubjectId(subject.id);
                        }}
                        className="p-2 border-inherit border-2 hover:border-black hover:bg-yellow-200 rounded hover:cursor-pointer"
                      >
                        <PencilSimple size={23} />
                      </button>
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDeleteSubject(subject.id);
                        }}
                        className="p-2 border-inherit border-2 bg-black hover:border-black hover:bg-pink-600 rounded hover:cursor-pointer"
                      >
                        <Trash size={23} color="white" />
                      </button>
                    </div>
                  </div>
                )}
              </li>
            );
          })}
        </ul>

        {/* Add new subject */}
        <div className="mt-6 border-t-2 pt-4">
          <h2 className="text-lg font-bold mb-2">Add New Subject</h2>
          <SubjectForm mode="add" onSave={handleAddSubject} />
        </div>
      </ContentWrapper>
    </PageWrapper>
  );
};

export default SubjectsPage;
